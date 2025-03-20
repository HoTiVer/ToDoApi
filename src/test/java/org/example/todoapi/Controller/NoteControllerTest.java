package org.example.todoapi.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.todoapi.DTO.ApiResponse;
import org.example.todoapi.DTO.NoteDTO;
import org.example.todoapi.DTO.ResponseWrapper;
import org.example.todoapi.config.SecurityConfig;
import org.example.todoapi.controller.NoteController;
import org.example.todoapi.entity.Note;
import org.example.todoapi.entity.User;
import org.example.todoapi.service.JwtService;
import org.example.todoapi.service.MyUserDetailsService;
import org.example.todoapi.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = NoteController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private MyUserDetailsService userDetailsService;

    private User user;
    private Note note1;
    private Note note2;

    @BeforeEach
    public void setup() {
        user = new User("user", "email", "password");

        note1 = new Note("test1", "test1", user);
        note1.setId(1L);
        note2 = new Note("test2", "test2", user);
        note2.setId(2L);
        user.setNotes(List.of(note1, note2));
    }

    @Test
    @WithMockUser(username = "email", password = "password")
    public void getAllNotes_ReturnResponseEntityInResponseWrapper() throws Exception {
        ResponseWrapper<Note> responseWrapper =
                new ResponseWrapper<>(user.getNotes(), 1, 10, 2L);

        when(noteService.getAllNotes(1, 10)).thenReturn(ResponseEntity.ok(responseWrapper));


        ResultActions resultActions = mockMvc.perform(get("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("limit", "10"));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.data[0].title").value("test1"));
    }

    @Test
    @WithMockUser(username = "email", password = "password")
    public void addNote_ReturnNote() throws Exception {
        NoteDTO noteDTO = new NoteDTO("test1", "test1");

        when(noteService.addNote(Mockito.any(NoteDTO.class))).thenReturn(note1);

        String token = generateTestToken();

        ResultActions resultActions = mockMvc.perform(post("/todos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.title").value("test1"));
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.description").value("test1"));
    }

    private String generateTestToken() {
        String secretKey = "thisIsASecretKeyWhichIsAtLeast32BytesLongForHS256";

        return Jwts.builder()
                .setSubject("email")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    @Test
    @WithMockUser(username = "email", password = "password")
    public void updateNote_UpdateTitleAndDescription_ReturnApiResponseNote() throws Exception {
        long noteId = 1;
        NoteDTO noteDTO = new NoteDTO("test2", "test2");

        ResponseEntity<ApiResponse> apiResponse=
                ResponseEntity.ok(new ApiResponse(true, "Note edited.", note1));

        when(noteService.editNote(Mockito.any(NoteDTO.class), Mockito.anyLong()))
                .thenReturn(apiResponse);


        ResultActions resultActions = mockMvc.perform(put("/todos/" + noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.success").value(true));
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.message").value("Note edited."));
    }

    @Test
    @WithMockUser(username = "email", password = "password")
    public void updateNote_UpdateTitle_ReturnApiResponse() throws Exception {
        long noteId = 1;
        NoteDTO noteDTO = new NoteDTO("test2", null);

        note1.setTitle("test2");
        ResponseEntity<ApiResponse> apiResponse=
                ResponseEntity.ok(new ApiResponse(true, "Note edited.", note1));

        when(noteService.editNote(Mockito.any(NoteDTO.class), Mockito.anyLong()))
                .thenReturn(apiResponse);


        ResultActions resultActions = mockMvc.perform(put("/todos/" + noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.success").value(true));
        resultActions.andExpect(MockMvcResultMatchers
                .jsonPath("$.message").value("Note edited."));
    }

    @Test
    public void deleteNote_ReturnVoid() {
        long noteId = 1L;
        doNothing().when(noteService).deleteNote(noteId);

        noteService.deleteNote(noteId);

        verify(noteService, times(1)).deleteNote(noteId);
    }
}

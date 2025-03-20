package org.example.todoapi.Service;

import org.example.todoapi.DTO.ApiResponse;
import org.example.todoapi.DTO.NoteDTO;
import org.example.todoapi.DTO.ResponseWrapper;
import org.example.todoapi.Repository.NoteRepository;
import org.example.todoapi.Repository.UserRepository;
import org.example.todoapi.entity.Note;
import org.example.todoapi.entity.User;
import org.example.todoapi.service.NoteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note1;
    private Note note2;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("user", "email", "password");
        userRepository.save(user);
        note1 = new Note("test1", "test1", user);
        note2 = new Note("test2", "test2", user);
        user.setNotes(List.of(note1, note2));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken("email", null));
        SecurityContextHolder.setContext(context);
    }

    @Test
    @WithMockUser(username = "email")
    public void getAllNotes_ReturnResponseEntityInResponseWrapper(){
        when(userRepository.findByEmail("email")).thenReturn(user);

        ResponseEntity<ResponseWrapper<Note>> responseEntity =
                noteService.getAllNotes(1, 10);


        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(200, responseEntity.getStatusCode().value());
        Assertions.assertEquals(2, responseEntity.getBody().getTotal());
        Assertions.assertEquals("test1",
                responseEntity.getBody().getData().get(0).getTitle());
        Assertions.assertEquals("test2",
                responseEntity.getBody().getData().get(1).getTitle());
    }

    @Test
    public void addNote_ReturnNote(){
        when(userRepository.findByEmail("email")).thenReturn(user);
        NoteDTO noteDTO = new NoteDTO("test1", "test1");

        Note returnedNote = noteService.addNote(noteDTO);

        Assertions.assertNotNull(returnedNote);
        Assertions.assertEquals(note1.getTitle(), returnedNote.getTitle());
    }

    @Test
    public void updateNote_UpdateTitleAndDescription_ReturnApiResponseNote(){
        user.setId(1L);
        note1.setId(1L);

        when(noteRepository.getUserId(user.getId())).thenReturn(user.getId());
        when(userRepository.findIdByEmail("email")).thenReturn(user.getId());

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note1));

        ResponseEntity<ApiResponse> noteEditedResponse = noteService
                .editNote(new NoteDTO("updated title", "updated description"), 1L);

        Assertions.assertNotNull(noteEditedResponse.getBody());
        Assertions.assertEquals(200, noteEditedResponse.getStatusCode().value());
        Assertions.assertTrue(noteEditedResponse.getBody().isSuccess());
        Assertions.assertEquals("updated title",
                noteEditedResponse.getBody().getData().getTitle());
        Assertions.assertEquals("updated description",
                noteEditedResponse.getBody().getData().getDescription());
    }

    @Test
    public void updateNote_UpdateTitle_ReturnApiResponseNote(){
        user.setId(1L);
        note1.setId(1L);

        when(noteRepository.getUserId(user.getId())).thenReturn(user.getId());
        when(userRepository.findIdByEmail("email")).thenReturn(user.getId());

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note1));

        ResponseEntity<ApiResponse> noteEditedResponse = noteService
                .editNote(new NoteDTO("updated title", null), 1L);

        Assertions.assertNotNull(noteEditedResponse.getBody());
        Assertions.assertEquals(200, noteEditedResponse.getStatusCode().value());
        Assertions.assertTrue(noteEditedResponse.getBody().isSuccess());
        Assertions.assertEquals("updated title",
                noteEditedResponse.getBody().getData().getTitle());
        Assertions.assertNotNull(noteEditedResponse.getBody().getData().getDescription());
    }

    @Test
    public void deleteNote_ReturnVoid(){
        user.setId(1L);
        note1.setId(1L);
        doNothing().when(noteRepository).deleteById(note1.getId());

        noteService.deleteNote(note1.getId());

        verify(noteRepository, times(1)).deleteById(note1.getId());
    }
}

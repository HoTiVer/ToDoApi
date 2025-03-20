package org.example.todoapi.controller;

import org.example.todoapi.DTO.ApiResponse;
import org.example.todoapi.DTO.NoteDTO;
import org.example.todoapi.DTO.ResponseWrapper;
import org.example.todoapi.entity.Note;
import org.example.todoapi.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/todos")
    public ResponseEntity<ResponseWrapper<Note>> getAllNotes(@RequestParam int page,
                                                             @RequestParam int limit){
        return noteService.getAllNotes(page, limit);
    }

    @PostMapping("/todos")
    public Note addNote(@RequestBody NoteDTO noteDTO){
        return noteService.addNote(noteDTO);
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<ApiResponse> editNote(@RequestBody NoteDTO noteDTO, @PathVariable Long id){
        return noteService.editNote(noteDTO, id);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
    }
}

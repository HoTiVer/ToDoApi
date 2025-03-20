package org.example.todoapi.service;

import jakarta.transaction.Transactional;
import org.example.todoapi.DTO.ApiResponse;
import org.example.todoapi.DTO.ResponseWrapper;
import org.example.todoapi.Repository.NoteRepository;
import org.example.todoapi.Repository.UserRepository;
import org.example.todoapi.DTO.NoteDTO;
import org.example.todoapi.entity.Note;
import org.example.todoapi.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public NoteService(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    public ResponseEntity<ResponseWrapper<Note>> getAllNotes(int page, int limit) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(name);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Note> notes = user.getNotes();
        List<Note> notesToReturn = new ArrayList<>();

        int startIndex = (page - 1) * limit;
        int endIndex = startIndex + limit;
        long total = (long) notes.size();

        for (int i = startIndex; i < endIndex && i < notes.size(); i++) {
            notesToReturn.add(notes.get(i));
        }

        ResponseWrapper<Note> responseWrapper =
                new ResponseWrapper<>(notesToReturn, page, limit, total);

        return ResponseEntity.ok(responseWrapper);
    }

    public Note addNote(NoteDTO noteDTO) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(name);

        if (user != null) {
            Note note = new Note(noteDTO.getTitle(), noteDTO.getDescription(), user);
            noteRepository.save(note);
            return note;
        }

        return null;
    }

    public ResponseEntity<ApiResponse> editNote(NoteDTO noteDTO, Long id) {

        if (!isOwner(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Error: You are not allowed to edit this note."));
        }

        Optional<Note> optionalNote = noteRepository.findById(id);
        if (!optionalNote.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Error: Note not found."));
        }

        Note note = optionalNote.get();

        if (noteDTO.getTitle() != null) {
            note.setTitle(noteDTO.getTitle());
        }
        if (noteDTO.getDescription() != null) {
            note.setDescription(noteDTO.getDescription());
        }

        noteRepository.save(note);

        return ResponseEntity.ok(new ApiResponse(true, "Note edited.", note));
    }

    @Transactional
    public void deleteNote(Long id) {
        if (isOwner(id)) {
            noteRepository.deleteById(id);
        }
    }


    boolean isOwner(Long id) {
        return Objects.equals(noteRepository.getUserId(id),
                userRepository.findIdByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));

    }

}

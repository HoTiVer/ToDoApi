package org.example.todoapi.Repository;

import org.example.todoapi.entity.Note;
import org.example.todoapi.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private Note note;
    private User user;

    @BeforeEach
    public void setup(){
        user = new User("user", "email", "password");
        userRepository.save(user);

        note = new Note("test", "test", user);
        user.setNotes(List.of(note));
    }

    @Test
    public void getUserId_ReturnUserId(){
        noteRepository.save(note);

        Long userId = noteRepository.getUserId(note.getId());

        Assertions.assertNotNull(userId);
    }


}

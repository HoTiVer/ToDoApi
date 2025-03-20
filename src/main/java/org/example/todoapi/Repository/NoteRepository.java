package org.example.todoapi.Repository;

import org.example.todoapi.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("select n.user.id from Note n where n.id = :id")
    Long getUserId(@Param("id") Long id);

}

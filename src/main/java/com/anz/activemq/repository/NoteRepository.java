package com.anz.activemq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.anz.activemq.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}

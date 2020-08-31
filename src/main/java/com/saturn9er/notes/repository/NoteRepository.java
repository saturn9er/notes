package com.saturn9er.notes.repository;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerOrderByModificationDateTimeDesc(User user);

    @Query("SELECT DISTINCT note FROM Note note JOIN Share share ON share.note = note WHERE share.trustee = ?1 ORDER BY note.modificationDateTime DESC")
    List<Note> findByTrusteeOrderByModificationDateTimeDesc(User trustee);
}

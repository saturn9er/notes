package com.saturn9er.notes.repository;

import com.saturn9er.notes.model.NotesHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesHistoryRepository extends JpaRepository<NotesHistory, Long> {
}

package com.saturn9er.notes.service;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.User;

import java.util.List;
import java.util.Optional;

public interface BaseNoteService {
    void createNote(Note note, User user);

    List<Note> getAllNotes();

    List<Note> getAllUserNotesInDescendingModificationDateTimeOrder(User user);

    List<Note> getAllSharedUserNotesInDescendingModificationDateTimeOrder(User user);

    Optional<Note> getNoteById(long id);

    void updateNote(Note note, User submitter);

    void deleteNote(Note note);

    void saveNote(Note note, User submitter);

    void autosaveNote(Note note);

}

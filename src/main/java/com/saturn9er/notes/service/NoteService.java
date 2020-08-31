package com.saturn9er.notes.service;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.NotesHistory;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.repository.NoteRepository;
import com.saturn9er.notes.repository.NotesHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService implements BaseNoteService {

    private NoteRepository noteRepository;
    private NotesHistoryRepository notesHistoryRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, NotesHistoryRepository notesHistoryRepository) {
        this.noteRepository = noteRepository;
        this.notesHistoryRepository = notesHistoryRepository;
    }

    @Override
    public void createNote(Note note, User submitter) {
        final LocalDateTime now = LocalDateTime.now();
        note.setOwner(submitter);
        note.setCreationDateTime(now);
        note.setModificationDateTime(now);
        note.setHistory(addNewHistoryItem(note, submitter));
        noteRepository.save(note);
    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public List<Note> getAllUserNotesInDescendingModificationDateTimeOrder(User user) {
        return noteRepository.findByOwnerOrderByModificationDateTimeDesc(user);
    }

    @Override
    public List<Note> getAllSharedUserNotesInDescendingModificationDateTimeOrder(User trustee) {
        return noteRepository.findByTrusteeOrderByModificationDateTimeDesc(trustee);
    }

    @Override
    public Optional<Note> getNoteById(long id) {
        return noteRepository.findById(id);
    }

    @Override
    public void updateNote(Note note, User submitter) {
        saveNote(note, submitter);
    }

    @Override
    public void deleteNote(Note note) {
        noteRepository.delete(note);
    }

    @Override
    public void saveNote(Note note, User submitter) {
        LocalDateTime now = LocalDateTime.now();
        note.setModificationDateTime(now);
        note.setHistory(addNewHistoryItem(note, submitter));
        noteRepository.save(note);
    }

    @Override
    public void autosaveNote(Note note) {
        LocalDateTime now = LocalDateTime.now();
        note.setModificationDateTime(now);
        noteRepository.save(note);
    }


    private List<NotesHistory> addNewHistoryItem(Note note, User submitter) {
        NotesHistory historyItem = new NotesHistory(note);
        historyItem.setSubmitter(submitter);

        List<NotesHistory> historyList = note.getHistory();
        historyList.add(historyItem);
        notesHistoryRepository.save(historyItem);

        return historyList;
    }

}

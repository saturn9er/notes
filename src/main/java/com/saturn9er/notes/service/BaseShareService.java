package com.saturn9er.notes.service;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Permission;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;

import java.util.List;

public interface BaseShareService {
    void createShare(Note note, User trustee, Permission permissions);

    List<Share> getSharesByNote(Note note);

    void deleteShare(long id);

    List<Share> getSharesByTrusteeAndNote(User trustee, Note note);
}

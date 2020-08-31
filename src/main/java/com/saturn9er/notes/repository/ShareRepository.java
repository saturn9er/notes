package com.saturn9er.notes.repository;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, Long> {
    List<Share> getByNote(Note note);

    List<Share> getByTrustee(User trustee);

    List<Share> getByTrusteeAndAndNote(User trustee, Note note);
}

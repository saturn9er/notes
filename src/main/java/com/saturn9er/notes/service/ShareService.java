package com.saturn9er.notes.service;

import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Permission;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareService implements BaseShareService {

    private ShareRepository shareRepository;

    @Autowired
    public ShareService(ShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    @Override
    public void createShare(Note note, User trustee, Permission permissions) {
        List<Share> shares = shareRepository.getByNote(note);
        Share share = new Share(note, trustee, permissions);

        // check whether there are same permissions given to the same trustee
        boolean hasDuplicate = false;
        for (Share sh : shares) {
            if (sh.getTrustee().equals(trustee) && sh.getPermissions().equals(permissions)) {
                hasDuplicate = true;
                break;
            }
        }

        if (!hasDuplicate) {
            shareRepository.save(share);
        }
    }

    @Override
    public List<Share> getSharesByNote(Note note) {
        return shareRepository.getByNote(note);
    }

    @Override
    public void deleteShare(long id) {
        shareRepository.deleteById(id);
    }

    @Override
    public List<Share> getSharesByTrusteeAndNote(User trustee, Note note) {
        return shareRepository.getByTrusteeAndAndNote(trustee, note);
    }
}

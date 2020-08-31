package com.saturn9er.notes.controller;

import com.saturn9er.notes.dto.ShareDto;
import com.saturn9er.notes.exception.ResourceForbiddenException;
import com.saturn9er.notes.exception.ResourceNotFoundException;
import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Permission;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.service.BaseNoteService;
import com.saturn9er.notes.service.BaseShareService;
import com.saturn9er.notes.service.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/*
 *   Terms used:
 *   trustee - is a user who was shared a note with
 *   granter - is a user who granted the access to their own note to another user
 * */
@Controller
@RequestMapping("/share")
public class ShareController {

    private BaseShareService shareService;
    private BaseUserService userService;
    private BaseNoteService noteService;

    @Autowired
    public ShareController(BaseShareService shareService, BaseUserService userService, BaseNoteService noteService) {
        this.shareService = shareService;
        this.userService = userService;
        this.noteService = noteService;
    }

    /*
     * Mappings for trustees
     */
    @GetMapping()
    public String getIndexPage(Principal principal,
                               ModelMap model) {
        User trustee = userService.findByUsername(principal.getName());
        model.put("notes", noteService.getAllSharedUserNotesInDescendingModificationDateTimeOrder(trustee));
        return "share/index";
    }

    @GetMapping("/note/{id}")
    public String getSharedNoteDetailsPage(Principal principal,
                                           @PathVariable("id") long noteId,
                                           ModelMap model) {

        User trustee = userService.findByUsername(principal.getName());

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s requested a non-existent note #%d",
                                        trustee.getUsername(), noteId)));

        // if trying to access a note not shared with you throw ResourceForbiddenException (403)
        List<Share> shares = shareService.getSharesByTrusteeAndNote(trustee, note);
        if (shares.isEmpty()) {
            throw new ResourceForbiddenException(String.format("User %s requested a shared note owned by %s and not shared with them",
                    trustee.getUsername(), note.getOwner().getUsername()));
        }

        // it would throw an exception in case of illegal access
        Permission permission = getMightiestPermission(note, trustee);

        model.put("note", note);
        model.put("permission", permission);

        return "share/edit";
    }

    @PostMapping("/note/{noteId}/edit")
    public String submitEdit(Principal principal,
                             RedirectAttributes redirectAttributes,
                             @PathVariable("noteId") Long noteId,
                             @Valid Note updatedNote) {

        User trustee = userService.findByUsername(principal.getName());

        // retrieving note under modification from DB by ID provided by form
        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s tried to edit a non-existent note #%d",
                                        trustee.getUsername(), updatedNote.getId())));

        // this should throw an exception in case of illegal access
        getMightiestPermission(note, trustee);

        // updating note with new title and content only in case either of them was altered
        if (!note.getTitle().equals(updatedNote.getTitle()) || !note.getContent().equals(updatedNote.getContent())) {
            note.setTitle(updatedNote.getTitle());
            note.setContent(updatedNote.getContent());
            noteService.updateNote(note, trustee);
        }

        redirectAttributes.addFlashAttribute("save_success", "Note saved!");
        return "redirect:/share/note/" + noteId;
    }

    /*
     * Mappings for granters
     */
    @PostMapping("/note/{noteId}")
    public String postNewShareForm(Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   @PathVariable("noteId") Long noteId,
                                   @Valid @ModelAttribute("share") ShareDto shareDto) {

        Note note = getLegitGranterNote(principal, noteId);

        User trustee = userService.findByUsername(shareDto.getTrusteeUsername());
        if (trustee == null) {
            redirectAttributes.addFlashAttribute("username_not_found_warning", "A user with such username doesn't exist!");
            return "redirect:/note/" + noteId + "/edit";
        }

        // this should throw an exception in case of illegal access
        Permission permissions = shareDto.getPermissionValue();

        shareService.createShare(note, trustee, permissions);

        redirectAttributes.addFlashAttribute("grant_success", "Permission was successfully granted");
        return "redirect:/note/" + noteId + "/edit";
    }

    @PostMapping("/note/{noteId}/delete/{shareId}")
    public String delete(Principal principal,
                         RedirectAttributes redirectAttributes,
                         @PathVariable Long noteId,
                         @PathVariable Long shareId) {

        // it's here for validation purposes
        getLegitGranterNote(principal, noteId);

        shareService.deleteShare(shareId);

        redirectAttributes.addFlashAttribute("revoke_success", "Permission successfully revoked!");
        return "redirect:/note/" + noteId + "/edit";
    }


    private Note getLegitGranterNote(Principal principal, long noteId) {
        User granter = userService.findByUsername(principal.getName());

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s requested a non-existent note #%d",
                                        granter.getUsername(), noteId)));

        // if trying to access someone else's note throw ResourceForbiddenException (403)
        if (!note.getOwner().equals(granter)) {
            throw new ResourceForbiddenException(String.format("User %s requested a note owned by %s",
                    granter.getUsername(), note.getOwner().getUsername()));
        }

        return note;
    }

    private Permission getMightiestPermission(Note note, User user) {
        List<Share> shares = shareService.getSharesByTrusteeAndNote(user, note);

        if (shares.isEmpty()) {
            throw new ResourceForbiddenException(String.format("User %s requested a shared note owned by %s and not shared with them",
                    user.getUsername(), note.getOwner().getUsername()));
        }

        Permission permission = null;
        for (Share share : shares) {
            if (permission == Permission.READ_EDIT) {
                break;
            }
            permission = share.getPermissions();
        }

        return permission;
    }

}

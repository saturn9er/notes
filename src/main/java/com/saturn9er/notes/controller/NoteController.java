package com.saturn9er.notes.controller;

import com.saturn9er.notes.dto.NoteDto;
import com.saturn9er.notes.exception.ResourceForbiddenException;
import com.saturn9er.notes.exception.ResourceNotFoundException;
import com.saturn9er.notes.dto.ShareDto;
import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.service.BaseNoteService;
import com.saturn9er.notes.service.BaseShareService;
import com.saturn9er.notes.service.BaseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/note")
public class NoteController {

    Logger logger = LoggerFactory.getLogger(NoteController.class);

    private BaseNoteService noteService;
    private BaseUserService userService;
    private BaseShareService shareService;

    @Autowired
    public NoteController(BaseNoteService noteService, BaseUserService userService, BaseShareService shareService) {
        this.noteService = noteService;
        this.userService = userService;
        this.shareService = shareService;
    }

    @GetMapping()
    public String getIndexPage(Principal principal, ModelMap model) {
        User user = userService.findByUsername(principal.getName());
        model.put("notes", noteService.getAllUserNotesInDescendingModificationDateTimeOrder(user));
        return "note/index";
    }

    @GetMapping("/add")
    public String getCreatePage(ModelMap model) {
        model.addAttribute("note", new Note());
        return "note/create";
    }

    @PostMapping("/add")
    public String postNewNoteForm(Principal principal, ModelMap model, @Valid Note note, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/note/add";
        }

        User user = userService.findByUsername(principal.getName());
        noteService.createNote(note, user);

        logger.debug("User {} posted a new note {}", user.getUsername(), note);

        return "redirect:/note/" + note.getId() + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String getEditPage(Principal principal, @PathVariable("id") long noteId, ModelMap model) {

        Note note = getLegitNote(principal, noteId);

        List<Share> shares = shareService.getSharesByNote(note);

        Collections.reverse(note.getHistory());

        model.put("note", note);
        model.put("shares", shares);
        model.addAttribute("shareDto", new ShareDto());

        logger.debug("User {} posted a new note {}", principal.getName(), note);

        return "note/edit";
    }

    @PostMapping("/edit")
    public String postEditNoteForm(Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   @Valid Note updatedNote) {

        User owner = userService.findByUsername(principal.getName());

        Note note = getLegitNote(owner, updatedNote.getId());

        // updating note with new title and content only in case either of them was altered
        if (!note.getTitle().equals(updatedNote.getTitle()) || !note.getContent().equals(updatedNote.getContent())) {
            note.setTitle(updatedNote.getTitle());
            note.setContent(updatedNote.getContent());
            noteService.updateNote(note, owner);
            logger.debug("User {} edited a note {}", principal.getName(), note);
        } else {
            logger.debug("User {} tried to edit a note {} with unaltered title/content", principal.getName(), note);
        }

        redirectAttributes.addFlashAttribute("save_success", "Note saved!");
        return "redirect:/note/" + updatedNote.getId() + "/edit";
    }

    @GetMapping("/{id}/remove")
    public String getRemove(Principal principal,
                            RedirectAttributes redirectAttributes,
                            @PathVariable("id") long noteId) {

        Note note = getLegitNote(principal, noteId);

        logger.debug("User {} deleted a note {}", principal.getName(), note);

        noteService.deleteNote(note);

        redirectAttributes.addFlashAttribute("remove_success", "Note was successfully removed");
        return "redirect:/note";
    }

    @MessageMapping("/note/{id}")
    @SendTo("/topic/note/{id}")
    public NoteDto coed(NoteDto note, @DestinationVariable("id") Long noteId) {
        return new NoteDto(note.getTitle(), note.getContent());
    }

    @MessageMapping("/note/{id}/autosave")
    public void autosave(NoteDto noteDto, @DestinationVariable("id") Long noteId) {
        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Couldn't autosave a note #%d because it's been deleted",
                                        noteId)));

        if (!note.getTitle().equals(noteDto.getTitle()) || !note.getContent().equals(noteDto.getContent())) {
            note.setTitle(noteDto.getTitle());
            note.setContent(noteDto.getContent());
            noteService.autosaveNote(note);
        }
    }


    private Note getLegitNote(Principal principal, long noteId) {
        User user = userService.findByUsername(principal.getName());

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s requested a non-existent note #%d",
                                        user.getUsername(), noteId)));

        // if trying to access someone else's note throw ResourceForbiddenException (403)
        if (!note.getOwner().equals(user)) {
            throw new ResourceForbiddenException(String.format("User %s requested a note owned by %s",
                    user.getUsername(), note.getOwner().getUsername()));
        }

        return note;
    }

    private Note getLegitNote(User user, long noteId) {

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s requested a non-existent note #%d",
                                        user.getUsername(), noteId)));

        // if trying to access someone else's note throw ResourceForbiddenException (403)
        if (!note.getOwner().equals(user)) {
            throw new ResourceForbiddenException(String.format("User %s requested a note owned by %s",
                    user.getUsername(), note.getOwner().getUsername()));
        }

        return note;
    }

}

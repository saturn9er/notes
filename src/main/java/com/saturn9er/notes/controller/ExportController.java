package com.saturn9er.notes.controller;

import com.saturn9er.notes.exception.ResourceForbiddenException;
import com.saturn9er.notes.exception.ResourceNotFoundException;
import com.saturn9er.notes.exporter.DocsExporter;
import com.saturn9er.notes.exporter.JsonExporter;
import com.saturn9er.notes.exporter.XmlExporter;
import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.Share;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.service.BaseNoteService;
import com.saturn9er.notes.service.BaseShareService;
import com.saturn9er.notes.service.BaseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * The type Export controller.
 */
@Controller
@RequestMapping("/export")
public class ExportController {

    private Logger logger = LoggerFactory.getLogger(ExportController.class);

    private BaseUserService userService;
    private BaseNoteService noteService;
    private BaseShareService shareService;

    /**
     * Instantiates a new Export controller.
     *
     * @param userService  the user service
     * @param noteService  the note service
     * @param shareService the share service
     */
    @Autowired
    public ExportController(BaseUserService userService, BaseNoteService noteService, BaseShareService shareService) {
        this.userService = userService;
        this.noteService = noteService;
        this.shareService = shareService;
    }


    /**
     * Gets xml note.
     *
     * @param principal the principal
     * @param noteId    the note id
     * @return the xml note
     * @throws IOException the io exception
     */
    @GetMapping(value = "note/{id}/xml", produces = MediaType.TEXT_XML_VALUE)
    public @ResponseBody
    ResponseEntity<Resource> getXmlNote(Principal principal, @PathVariable("id") long noteId)
            throws IOException {

        Note note = getLegitNote(principal, noteId);
        XmlExporter xmlExporter = new XmlExporter();
        File file = xmlExporter.export(note);
        logger.info("User {} exported a note #{} to XML", principal.getName(), noteId);
        return getSuccessDownloadResponseEntry(file, MediaType.TEXT_XML);
    }

    /**
     * Gets json note.
     *
     * @param principal the principal
     * @param noteId    the note id
     * @return the json note
     * @throws IOException the io exception
     */
    @GetMapping(value = "note/{id}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Resource> getJsonNote(Principal principal, @PathVariable("id") long noteId)
            throws IOException {

        Note note = getLegitNote(principal, noteId);
        JsonExporter jsonExporter = new JsonExporter();
        File file = jsonExporter.export(note);
        logger.info("User {} exported a note #{} to JSON", principal.getName(), noteId);
        return getSuccessDownloadResponseEntry(file, MediaType.APPLICATION_JSON);
    }

    /**
     * Gets docx note.
     *
     * @param principal the principal
     * @param noteId    the note id
     * @return the docx note
     * @throws IOException the io exception
     */
    @GetMapping(value = "note/{id}/docx", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<Resource> getDocxNote(Principal principal, @PathVariable("id") long noteId)
            throws IOException {

        Note note = getLegitNote(principal, noteId);
        DocsExporter docsExporter = new DocsExporter();
        File file = docsExporter.export(note);
        logger.info("User {} exported a note #{} to DOCX", principal.getName(), noteId);
        return getSuccessDownloadResponseEntry(file, MediaType.APPLICATION_OCTET_STREAM);
    }


    private Note getLegitNote(Principal principal, long noteId) {
        User user = userService.findByUsername(principal.getName());

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User %s tried to export a non-existent note #%d",
                                        user.getUsername(), noteId)));


        List<Share> shares = shareService.getSharesByTrusteeAndNote(user, note);

        // If trying to access someone else's note or a note not shared with requester throw ResourceForbiddenException (403)
        if (!note.getOwner().equals(user) && shares.isEmpty()) {
            throw new ResourceForbiddenException(String.format("User %s tried to export a note owned by %s",
                    user.getUsername(), note.getOwner().getUsername()));
        }

        return note;
    }

    private ResponseEntity<Resource> getSuccessDownloadResponseEntry(File file, MediaType mediaType) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "attachment;filename=" + file.getName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }
}

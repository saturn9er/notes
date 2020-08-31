package com.saturn9er.notes.controller;

import com.saturn9er.notes.importer.Importable;
import com.saturn9er.notes.importer.ImporterFactory;
import com.saturn9er.notes.model.Note;
import com.saturn9er.notes.model.User;
import com.saturn9er.notes.service.BaseNoteService;
import com.saturn9er.notes.service.BaseStorageService;
import com.saturn9er.notes.service.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.security.Principal;

@Controller
@RequestMapping("/import")
public class ImportController {

    private final BaseStorageService storageService;
    private final BaseUserService userService;
    private final BaseNoteService noteService;

    @Autowired
    public ImportController(BaseStorageService storageService,
                            BaseUserService userService,
                            BaseNoteService noteService) {
        this.storageService = storageService;
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping()
    public String handleFileUpload(Principal principal,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try {
            storageService.store(file); // save a file to the db
            ImporterFactory<Note> importerFactory = new ImporterFactory<>();
            Importable<Note> importer = importerFactory.getImporter(file, Note.class); // constructing new importer based on type
            Note note = importer.getObjectFromFile(file); // mapping file to note
            User owner = userService.findByUsername(principal.getName());
            noteService.createNote(note, owner);
        } catch (FileSystemException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error occurred while uploading " + file.getOriginalFilename() + "!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error occurred while trying to read an object from a file: " + file.getOriginalFilename() + "!");
        }

        return "redirect:/note";
    }

}

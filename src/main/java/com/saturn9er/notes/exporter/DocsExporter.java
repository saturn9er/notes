package com.saturn9er.notes.exporter;

import com.saturn9er.notes.model.Note;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DocsExporter implements Exportable {
    private File file = new File("note.docx");

    private static final Logger logger = LoggerFactory.getLogger(DocsExporter.class);

    @Override
    public File export(Note note) throws IOException {
        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
            mainDocumentPart.addStyledParagraphOfText("Title", note.getTitle());
            mainDocumentPart.addParagraphOfText(note.getContent());
            wordPackage.save(file);
            logger.debug("Note {} mapped to docx", note.getId());
        } catch (Exception e) {
            logger.error("Error occurred while trying to write content to a docx file: {}", e.getMessage());
        }
        return file;
    }
}

package com.saturn9er.notes.importer;

import com.saturn9er.notes.converter.MultipartFileToFileConverter;
import com.saturn9er.notes.model.Note;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class NoteDocxImporter implements Importable<Note> {

    private static final Logger logger = LoggerFactory.getLogger(NoteDocxImporter.class);

    @Override
    public Note getObjectFromFile(MultipartFile multipartFile) throws IOException {
        Note note = new Note();
        File file;

        try {
            file = MultipartFileToFileConverter.convert(multipartFile);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
            final String XPATH_TO_SELECT_TEXT_NODES = "//w:p"; // this is something like a paragraph splitter
            List<Object> paragraphs = mainDocumentPart.getJAXBNodesViaXPath(XPATH_TO_SELECT_TEXT_NODES, true);

            // mapping all document paragraphs to a note
            StringBuilder content = new StringBuilder();
            for (int i = 0; i < paragraphs.size(); i++) {
                String paragraph = paragraphs.get(i).toString();
                if (i == 0) { // first paragraph goes to a title
                    note.setTitle(paragraph);
                } else { // the rest go to content
                    content.append(paragraph + "\n");
                }
            }
            note.setContent(content.toString());

        } catch (IOException e) {
            logger.error("Error occurred while trying to read an object from a file: {}", e.getMessage());
            throw new IOException();
        } catch (Docx4JException | JAXBException e) {
            logger.error(e.getMessage());
        }

        return note;
    }

}

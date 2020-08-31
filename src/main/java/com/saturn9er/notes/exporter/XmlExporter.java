package com.saturn9er.notes.exporter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.saturn9er.notes.model.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class XmlExporter implements Exportable {
    private File file = new File("note.xml");

    private static final Logger logger = LoggerFactory.getLogger(XmlExporter.class);

    @Override
    public File export(Note note) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            xmlMapper.writeValue(file, note);
            logger.info("Note {} mapped to XML", note.getId());
        } catch (IOException e) {
            throw new IOException("Error occurred while trying to write a value to a file: " + e.getMessage());
        }

        return file;
    }
}

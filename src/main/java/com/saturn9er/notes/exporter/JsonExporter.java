package com.saturn9er.notes.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.saturn9er.notes.model.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class JsonExporter implements Exportable {
    private File file = new File("note.json");

    private static final Logger logger = LoggerFactory.getLogger(JsonExporter.class);

    @Override
    public File export(Note note) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(file, note);
            logger.info("Note {} mapped to JSON", note.getId());
        } catch (IOException e) {
            logger.error("Error occurred while trying to write a value to a file: {}", e.getMessage());
            throw new IOException();
        }

        return file;
    }
}

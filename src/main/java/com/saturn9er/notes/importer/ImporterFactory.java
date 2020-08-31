package com.saturn9er.notes.importer;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImporterFactory<T> {
    public Importable<T> getImporter(MultipartFile file, Class<T> type) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        switch (extension) {
            case "xml":
            case "json":
                return new Importer<T>(type);
            case "docx":
                return (Importable<T>) new NoteDocxImporter();
            default:
                throw new IllegalArgumentException("Could not find appropriate mapper for given extension: " + extension);
        }
    }
}

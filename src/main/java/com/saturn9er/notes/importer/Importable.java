package com.saturn9er.notes.importer;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface Importable<T> {
    T getObjectFromFile(MultipartFile file) throws IOException;
}

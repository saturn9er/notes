package com.saturn9er.notes.service;

import com.saturn9er.notes.model.DatabaseFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystemException;

public interface BaseStorageService {
    DatabaseFile store(MultipartFile file) throws FileSystemException;

    MultipartFile get(String filename);
}

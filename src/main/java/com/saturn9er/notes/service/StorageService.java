package com.saturn9er.notes.service;

import com.saturn9er.notes.model.DatabaseFile;
import com.saturn9er.notes.repository.StorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.*;

@Service
public class StorageService implements BaseStorageService {

    Logger logger = LoggerFactory.getLogger(StorageService.class);

    private static final List<String> allowedContentTypes;

    private static final String[] allowedContents = {
            "application/json",
            "text/xml",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    static {
        allowedContentTypes = new ArrayList<>(Arrays.asList(allowedContents));
    }

    private StorageRepository storageRepository;

    @Autowired
    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @Override
    public DatabaseFile store(MultipartFile file) throws FileSystemException{
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileSystemException("Filename contains invalid path sequence " + fileName);
            }

            // if provided not allowed file type
            if (!allowedContentTypes.contains(file.getContentType())) {
                logger.error("Wrong type of file {}. Type provided: {}", fileName, file.getContentType());
                throw new FileSystemException("Wrong type of file " + fileName);
            }

            DatabaseFile databaseFile = new DatabaseFile(fileName, file.getContentType(), file.getBytes());

            return storageRepository.save(databaseFile);
        } catch (IOException ex) {
            throw new FileSystemException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Override
    public MultipartFile get(String filename) {
        return null;
    }
}

package com.saturn9er.notes.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saturn9er.notes.converter.MultipartFileToFileConverter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class Importer<T> implements Importable<T> {

    private final static Logger logger = LoggerFactory.getLogger(Importer.class);

    private final Class<T> type;

    public Importer(Class<T> type) {
        this.type = type;
    }

    @Override
    public T getObjectFromFile(MultipartFile multipartFile) throws IOException, IllegalArgumentException {
        T t;
        File file;
        ObjectMapper mapper;
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()); // get extension on a file

        try {
            mapper = MapperFactory.getMapperByFileExtension(extension);
            file = MultipartFileToFileConverter.convert(multipartFile);
            t = mapper.readValue(file, type);
        } catch (IOException e) {
            logger.error("Error occurred while trying to read an object from a file: " + e.getMessage());
            throw new IOException();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

        return t;
    }

}

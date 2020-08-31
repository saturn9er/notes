package com.saturn9er.notes.converter;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class MultipartFileToFileConverter {

    private MultipartFileToFileConverter() {}

    public static File convert(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("temp_", multipartFile.getOriginalFilename());
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        file.deleteOnExit();
        return file;
    }
}

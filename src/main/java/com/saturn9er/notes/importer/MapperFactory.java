package com.saturn9er.notes.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class MapperFactory {
    private MapperFactory() {}
    public static ObjectMapper getMapperByFileExtension(String extension) throws IllegalArgumentException {
        switch (extension) {
            case "xml":
                return new XmlMapper();
            case "json":
                return new ObjectMapper();
            default:
                throw new IllegalArgumentException("Could not find appropriate mapper for given extension: " + extension);
        }
    }
}

package com.saturn9er.notes.exporter;

import com.saturn9er.notes.model.Note;

import java.io.File;
import java.io.IOException;

public interface Exportable {
    File export(Note note) throws IOException;
}

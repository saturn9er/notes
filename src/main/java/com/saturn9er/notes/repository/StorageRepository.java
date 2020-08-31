package com.saturn9er.notes.repository;

import com.saturn9er.notes.model.DatabaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<DatabaseFile, String> {

}

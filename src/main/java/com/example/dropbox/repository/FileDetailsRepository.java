package com.example.dropbox.repository;

import com.example.dropbox.data.model.FileDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileDetailsRepository extends CrudRepository<FileDetails, String> {
    Optional<FileDetails> getFileDetailsByFileId(String fileId);
    void deleteByFileId(String fileId);
}

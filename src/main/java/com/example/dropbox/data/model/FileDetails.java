package com.example.dropbox.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Document("files")
public class FileDetails {
    @Id
    private String fileId;
    private String fileName;
    private String fileType;
    private long createdOn;
    private long lastModifiedOn;
    private long size;
    private String path;

    public static FileDetails newEntry(String fileName, String fileType, long size, String path) {
        return new FileDetails(UUID.randomUUID().toString(), fileName, fileType, System.currentTimeMillis(),
            System.currentTimeMillis(), size, path);
    }
}

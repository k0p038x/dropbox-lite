package com.example.dropbox.data.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class FileMetadataResponseDto {
    private String fileId;
    private String fileName;
    private String fileType;
    private long createdOn;
    private long lastUpdatedOn;
    private long sizeInBytes;
}

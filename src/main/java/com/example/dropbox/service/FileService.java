package com.example.dropbox.service;

import com.example.dropbox.data.dto.FileMetadataRequestDto;
import com.example.dropbox.data.dto.FileMetadataResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileMetadataResponseDto> getFilesMetadata();
    FileMetadataResponseDto updateFile(String fileId, MultipartFile file, FileMetadataRequestDto metadata);
    FileMetadataResponseDto uploadFile(MultipartFile file, FileMetadataRequestDto metadata);
    boolean deleteFile(String fileId);
    Resource getFileContent(String fileId);
}

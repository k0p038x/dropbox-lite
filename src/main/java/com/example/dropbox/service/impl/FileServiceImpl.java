package com.example.dropbox.service.impl;

import com.example.dropbox.data.dto.FileMetadataRequestDto;
import com.example.dropbox.data.dto.FileMetadataResponseDto;
import com.example.dropbox.data.model.FileDetails;
import com.example.dropbox.exception.BadRequestException;
import com.example.dropbox.exception.ServerErrorException;
import com.example.dropbox.repository.FileDetailsRepository;
import com.example.dropbox.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileServiceImpl implements FileService {
    private final ModelMapper modelMapper;
    private final FileDetailsRepository fileDetailsRepository;

    @Override
    public List<FileMetadataResponseDto> getFilesMetadata() {
        List<FileMetadataResponseDto> res = new ArrayList<>();
        fileDetailsRepository.findAll().forEach(entry -> res.add(modelMapper.map(entry, FileMetadataResponseDto.class)));
        return res;
    }

    private FileDetails getFileDetailsEntry(String fileId) {
        return fileDetailsRepository.getFileDetailsByFileId(fileId).orElseThrow(
            () -> new BadRequestException("invalid file id"));
    }

    @Override
    public FileMetadataResponseDto updateFile(String fileId, MultipartFile multipartFile, FileMetadataRequestDto metadata) {
        if (multipartFile == null && metadata == null) {
            throw new BadRequestException("atleast file or metadata should be present");
        }
        log.info("FileId: {}, updating file", fileId);
        FileDetails fileDetails = getFileDetailsEntry(fileId);
        if (multipartFile != null) {
            Path path = getRandomFilePath();
            try {
                multipartFile.transferTo(path);
            } catch (IOException e) {
                log.error("Unable to transfer file to path: {}", path, e);
                throw new ServerErrorException("file updated failed");
            }
            fileDetails.setPath(path.toString());
            log.info("FileId: {}, updated file content", fileId);
        }

        if (metadata != null) {
            if (metadata.getFileName() != null)
                fileDetails.setFileName(metadata.getFileName());
            log.info("FileId: {}, updated file metadata", fileId);
        }

        fileDetails.setLastModifiedOn(System.currentTimeMillis());
        fileDetails = fileDetailsRepository.save(fileDetails);
        log.info("FileId: {}, saved updated file details entry", fileId);
        return modelMapper.map(fileDetails, FileMetadataResponseDto.class);
    }

    @Override
    public FileMetadataResponseDto uploadFile(MultipartFile multipartFile, FileMetadataRequestDto metadata) {
        if (multipartFile == null || metadata == null) {
            throw new BadRequestException("file and metadata should be present");
        }
        Path path = getRandomFilePath();
        try {
            multipartFile.transferTo(path);
        } catch (IOException e) {
            log.error("Unable to transfer file to path: {}", path, e);
            throw new ServerErrorException("file updated failed");
        }
        String fileName = metadata.getFileName();
        String type = getFileType(multipartFile.getName());
        FileDetails fileDetails = FileDetails.newEntry(fileName, type, multipartFile.getSize(), path.toString());
        fileDetailsRepository.save(fileDetails);
        log.info("FileId: {}, created file", fileDetails.getFileId());
        return modelMapper.map(fileDetails, FileMetadataResponseDto.class);
    }

    @Override
    public boolean deleteFile(String fileId) {
        FileDetails fileDetails = getFileDetailsEntry(fileId);
        File file = new File(fileDetails.getPath());
        boolean status = file.delete();
        if (status) {
            fileDetailsRepository.deleteByFileId(fileId);
            log.info("FileId: {}, deleted file details entry", fileId);
        } else {
            log.warn("FileId: {}, unable to delete content file", fileId);
        }
        return status;
    }

    @Override
    public Resource getFileContent(String fileId) {
        FileDetails fileDetails = getFileDetailsEntry(fileId);
        try {
            return new ByteArrayResource(Files.readAllBytes(Path.of(fileDetails.getPath())));
        } catch (IOException e) {
            throw new ServerErrorException("unable to read file");
        }
    }

    private String getFileType(String fileName) {
        String[] splits = fileName.split("\\.");
        if (splits.length < 2)
            return "";
        else
            return splits[1];
    }

    private Path getRandomFilePath() {
        String dir = "/tmp/";
        String fileName = UUID.randomUUID().toString();
        return Path.of(dir + fileName);
    }
}

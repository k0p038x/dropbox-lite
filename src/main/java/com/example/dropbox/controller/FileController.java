package com.example.dropbox.controller;

import com.example.dropbox.data.dto.FileMetadataRequestDto;
import com.example.dropbox.data.dto.FileMetadataResponseDto;
import com.example.dropbox.exception.BadRequestException;
import com.example.dropbox.service.FileService;
import com.example.dropbox.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/v1/files")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileController {
    private final FileService fileService;
    private final ObjectMapper mapper = ObjectMapperFactory.getSnakeCaseObjectMapper();

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<FileMetadataResponseDto> getFiles() {
        return fileService.getFilesMetadata();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public FileMetadataResponseDto uploadFile(@RequestParam("metadata") String metadataAsStr, @RequestParam("file") MultipartFile file) {
        FileMetadataRequestDto metadata = null;
        try {
            if (StringUtils.hasLength(metadataAsStr))
                metadata = mapper.readValue(metadataAsStr, FileMetadataRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("invalid metadata value");
        }
        return fileService.uploadFile(file, metadata);
    }

    @GetMapping(value = "/{fileId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Resource getFile(@PathVariable String fileId) {
        return fileService.getFileContent(fileId);
    }

    @PutMapping("/{fileId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileMetadataResponseDto updateFile(@PathVariable String fileId, @RequestParam(value = "metadata", required = false) String metadataAsStr, @RequestParam("file") MultipartFile file) {
        FileMetadataRequestDto metadata = null;
        try {
            if (StringUtils.hasLength(metadataAsStr))
                metadata = mapper.readValue(metadataAsStr, FileMetadataRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("invalid metadata value");
        }
        return fileService.updateFile(fileId, file, metadata);
    }

    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean deleteFile(@PathVariable String fileId) {
        return fileService.deleteFile(fileId);
    }

}

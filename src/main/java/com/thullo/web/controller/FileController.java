package com.thullo.web.controller;

import com.thullo.data.model.Files;
import com.thullo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo")
public class FileController {
    private final FileService fileService ;

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileId") String fileId) {
        try {
            Files files = fileService.getFIle(fileId);
            Resource resource = new ByteArrayResource(files.getFileData());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + files.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return fileService.uploadFile(file, url);
    }

}

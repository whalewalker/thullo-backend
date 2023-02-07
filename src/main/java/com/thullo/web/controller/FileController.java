package com.thullo.web.controller;

import com.thullo.data.model.FileData;
import com.thullo.service.FileService;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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
    private final FileService fileService;

    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable("fileId") String fileId) {
        try {
            FileData files = fileService.getFIle(fileId);
            ByteArrayResource resource = new ByteArrayResource(files.getFileByte());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + files.getFileName())
                    .contentType(fileService.getMediaTypeForFileType(files.getFileType()))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        try {
            return ResponseEntity.ok(new ApiResponse(true, "File successfully uploaded",
                    fileService.uploadFile(file, url)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

package com.thullo.web.controller;

import com.thullo.data.model.FileData;
import com.thullo.service.FileService;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/files")
@Slf4j
public class FileController {
    private final FileService fileService;

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable("fileId") String fileId, @RequestParam(required = false, defaultValue = "false") boolean asAttachment) {
        try {
            FileData file = fileService.getFile(fileId.contains(".") ? fileId.substring(0, fileId.lastIndexOf(".")) : fileId);
            byte[] fileContent = file.getFileByte();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(fileService.getMediaTypeForFileType(file.getFileType()));
            headers.setContentLength(fileContent.length);
            headers.setContentDisposition(ContentDisposition.builder(asAttachment ? "attachment" : "inline").filename(file.getFileName()).build());
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileContent));
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
        }
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") String fileId) {
        try {
            FileData files = fileService.getFile(fileId);
            ByteArrayResource resource = new ByteArrayResource(files.getFileByte());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + files.getFileName());
            headers.setContentType(fileService.getMediaTypeForFileType(files.getFileType()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "File not found"));
        }
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

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable("fileId") String fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(new ApiResponse(true, "File deleted successfully"));
    }
}

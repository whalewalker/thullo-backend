package com.thullo.web.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardRequest {
    private String name;
    private MultipartFile file;
}

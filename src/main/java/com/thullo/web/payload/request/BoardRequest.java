package com.thullo.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {
    @NotBlank(message = "Board name cannot be blank")
    private String boardName;
    private String requestUrl;
    @NotNull(message = "Cover image cannot be null")
    private MultipartFile file;
    private String boardVisibility;     //The board visibility is set ot private by default
    private String boardTag;
    private String imageUrl;
}
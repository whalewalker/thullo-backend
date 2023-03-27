package com.thullo.web.payload.request;

import com.thullo.data.model.BoardVisibility;
import com.thullo.web.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.thullo.data.model.BoardVisibility.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {
    private String name;
    private String requestUrl;
    private MultipartFile file;
    //The board visibility is set ot private by default
    private String boardVisibility;
}
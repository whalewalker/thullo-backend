package com.thullo.web.payload.request;

import com.thullo.data.model.BoardVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBoardRequest {
    private String name;
    private String requestUrl;
    private MultipartFile file;

    //The board visibility is set ot private by default
    private String boardVisibility;
    private String boardTag;
}

package com.thullo.web.payload.request;

import com.thullo.data.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String name;
    private String requestUrl;
    private MultipartFile file;

    private Long taskColumnId;

    private Task task;

}

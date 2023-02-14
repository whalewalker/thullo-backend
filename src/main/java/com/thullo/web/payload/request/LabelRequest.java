package com.thullo.web.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LabelRequest {
    @NotBlank(message = "Label name cannot be blank")
    private String name;

    @NotBlank(message = "Label color code cannot be blank")
    private String colorCode;

    @NotBlank(message = "Label background code cannot be blank")
    private String backgroundCode;
}

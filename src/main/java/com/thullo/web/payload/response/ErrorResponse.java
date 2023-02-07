package com.thullo.web.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ErrorResponse {
    private boolean isSuccessful;
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<ValidationError> errors;
    private String stackTrace;

    private Object data;

    public ErrorResponse() {
        isSuccessful = false;
        timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message) {
        this.isSuccessful = false;
        this.status = status;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }


    public void addValidationError(String field, String message){
        if(isNull(errors)){
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}

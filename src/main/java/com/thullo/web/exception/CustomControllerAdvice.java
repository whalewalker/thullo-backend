package com.thullo.web.exception;

import com.thullo.web.payload.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class CustomControllerAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage("File size exceeded maximum limit of 1 MB");
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        HttpStatus statusCode = ex.getStatusCode();
        if (statusCode == HttpStatus.FORBIDDEN) {
            log.info("Error occurred ==> {}", (Object) ex.getStackTrace());
            ErrorResponse errorResponse = new ErrorResponse( statusCode.value(), ex.getMessage());
            log.info("Error response ==> {}", errorResponse);
            return new ResponseEntity<>(errorResponse, statusCode);
        }
        return new ResponseEntity<>(ex.getStatusCode());
    }
}

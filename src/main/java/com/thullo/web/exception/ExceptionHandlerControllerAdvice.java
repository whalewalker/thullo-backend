package com.thullo.web.exception;

import com.thullo.web.payload.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.info("Error message ==> {}", ex.getMessage());
        log.info("Error stack trace ==> {}", (Object) ex.getStackTrace());
        ErrorResponse errorResponse = new ErrorResponse( HttpStatus.FORBIDDEN.value(), "Access Denied");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

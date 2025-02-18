package com.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.model.Response;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = {
            NotFoundException.class,
            ExistException.class
    })
    public ResponseEntity<Response<Object>> handleException(Exception ex) {
        Response<Object> res = new Response<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}

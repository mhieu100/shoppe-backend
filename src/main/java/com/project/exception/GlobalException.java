package com.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//import com.project.model.Response;

@RestControllerAdvice
public class GlobalException {
//    @ExceptionHandler(value = {
//            NotFoundException.class,
//    })
//    public ResponseEntity<Response<Object>> handleException(Exception ex) {
//        Response<Object> res = new Response<>();
//        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        res.setError(ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->{
            errors.put(error.getField(),error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyEmailExistException.class)
    public ResponseEntity<Map<String,String>> handleAlreadyEmailExistException(AlreadyEmailExistException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error",ex.getMessage()));
    }
}

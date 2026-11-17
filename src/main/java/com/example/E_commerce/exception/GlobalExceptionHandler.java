package com.example.E_commerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateEmailException exception){

        Map<String, Object> body = new HashMap<>();
        body.put("status", 409);
        body.put("error", "Duplicate");
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception){
        Map<String, Object> body = new HashMap<>();
        body.put("status", 404);
        body.put("error", "Not found");
        body.put("message", exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateSlugException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSlug(DuplicateSlugException exception){
        Map<String, Object> body = new HashMap<>();
        body.put("status", 409);
        body.put("error", "Duplicate");
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidParentCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidParent(InvalidParentCategoryException exception){
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Bad request");
        body.put("message", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

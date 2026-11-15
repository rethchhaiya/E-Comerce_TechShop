package com.example.E_commerce.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.E_commerce.exception.DuplicateEmailException;

public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateEmailException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", exception.getMessage()));
    }
}

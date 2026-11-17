package com.example.E_commerce.exception;

public class DuplicateSlugException extends RuntimeException{
    public DuplicateSlugException(String message){
        super(message);
    }
}

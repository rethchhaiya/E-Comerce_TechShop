package com.example.E_commerce.exception;

public class DuplicateSlugException extends DuplicateResourceException{
    public DuplicateSlugException(String message){
        super(message);
    }
}

package com.example.E_commerce.exception;

public class DuplicateEmailException extends DuplicateResourceException{
    public DuplicateEmailException(String message){
        super(message);
    }
}

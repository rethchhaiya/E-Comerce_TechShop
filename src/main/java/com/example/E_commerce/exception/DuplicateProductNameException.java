package com.example.E_commerce.exception;

public class DuplicateProductNameException extends DuplicateResourceException{
    public DuplicateProductNameException(String message){
        super(message);
    }
}

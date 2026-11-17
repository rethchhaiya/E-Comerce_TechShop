package com.example.E_commerce.exception;

public class DuplicateProductNameException extends RuntimeException{
    public DuplicateProductNameException(String message){
        super(message);
    }
}

package com.example.E_commerce.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message){
        super(message);
    }
}

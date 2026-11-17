package com.example.E_commerce.exception;

public class InvalidParentCategoryException extends RuntimeException{
    public InvalidParentCategoryException(String message){
        super(message);
    }
}

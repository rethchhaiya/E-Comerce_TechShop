package com.example.E_commerce.exception;

public class CategoryHasProductsException extends RuntimeException{
    public CategoryHasProductsException(String message){
        super(message);
    }
}

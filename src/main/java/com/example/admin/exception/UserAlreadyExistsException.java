package com.example.admin.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String s){
        super(s);
    }
}

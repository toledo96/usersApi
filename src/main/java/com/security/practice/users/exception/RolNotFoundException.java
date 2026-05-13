package com.security.practice.users.exception;

public class RolNotFoundException extends RuntimeException{
    public RolNotFoundException(String message) {
        super(message);
    }
}

package com.bahadircolak.library.web.advice.exception;

public class UserConflictException extends RuntimeException{

    public UserConflictException(String message) {
        super(message);
    }
}

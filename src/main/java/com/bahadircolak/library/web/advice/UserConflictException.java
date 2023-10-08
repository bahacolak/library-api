package com.bahadircolak.library.web.advice;

public class UserConflictException extends RuntimeException{

    public UserConflictException(String message) {
        super(message);
    }
}

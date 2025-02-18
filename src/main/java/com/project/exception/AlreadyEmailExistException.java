package com.project.exception;

public class AlreadyEmailExistException extends RuntimeException{
    public AlreadyEmailExistException(String message){
        super(message);
    }
}

package org.openjfx.miniprojet.dao;

public class DataAccessException extends RuntimeException{

    public DataAccessException(String message, Throwable cause){
        super(message, cause);
    }
}

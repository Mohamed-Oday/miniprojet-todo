package org.openjfx.miniprojet.dao;

import org.openjfx.miniprojet.utiil.Database;

public class DataAccessException extends RuntimeException{

    public DataAccessException(String message){
        super(message);
    }

    public DataAccessException(String message, Throwable cause){
        super(message, cause);
    }
}

package org.openjfx.miniprojet.dao;

/**
 * Custom exception class for data access errors.
 * This class extends RuntimeException and is used to handle exceptions related to database operations.
 */
public class DataAccessException extends RuntimeException {

    /**
     * Constructs a new DataAccessException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.github.jasoma.graft.deserialize;

/**
 * Exception thrown when a database entity cannot be converted into a local type.
 */
public class EntityDeserializationException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message a message describing the error.
     */
    public EntityDeserializationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message a message describing the error.
     * @param cause a root cause for the error.
     */
    public EntityDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

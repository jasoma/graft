package com.github.jasoma.graft.convert;

/**
 * Exception thrown when a database node cannot be converted into a local type.
 */
public class NodeDeserializationException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message a message describing the error.
     */
    public NodeDeserializationException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message a message describing the error.
     * @param cause a root cause for the error.
     */
    public NodeDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

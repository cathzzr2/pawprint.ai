package org.abx.virturalpet.exception;

public class S3GetException extends RuntimeException {
    public S3GetException(String message) {
        super(message);
    }

    public S3GetException(String message, Throwable cause) {
        super(message, cause);
    }
}

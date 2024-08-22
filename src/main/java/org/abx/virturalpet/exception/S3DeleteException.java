package org.abx.virturalpet.exception;

public class S3DeleteException extends RuntimeException {
    public S3DeleteException(String message) {
        super(message);
    }

    public S3DeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}

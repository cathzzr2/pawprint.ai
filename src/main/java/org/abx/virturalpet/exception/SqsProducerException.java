package org.abx.virturalpet.exception;

public class SqsProducerException extends RuntimeException {
    public SqsProducerException(String message) {
        super(message);
    }

    public SqsProducerException(String message, Throwable cause) {
        super(message, cause);
    }
}

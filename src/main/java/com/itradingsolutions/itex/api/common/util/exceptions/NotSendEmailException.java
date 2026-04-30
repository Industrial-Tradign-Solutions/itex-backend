package com.itradingsolutions.itex.api.common.util.exceptions;

public class NotSendEmailException extends RuntimeException {
    public NotSendEmailException(String message) {
        super(message);
    }

    public NotSendEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSendEmailException(Throwable cause) {
        super(cause);
    }
}

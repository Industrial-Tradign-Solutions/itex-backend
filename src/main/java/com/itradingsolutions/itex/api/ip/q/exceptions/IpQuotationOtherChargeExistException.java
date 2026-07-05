package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

/**
 * Exception thrown when attempting to create an other charge with a description
 * that already exists for the same quotation.
 * <p>
 * Each quotation must have unique other charge descriptions.
 * </p>
 */
public class IpQuotationOtherChargeExistException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 8674482801885684736L;

    /**
     * Constructs a new exception with no detail message.
     */
    public IpQuotationOtherChargeExistException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public IpQuotationOtherChargeExistException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public IpQuotationOtherChargeExistException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public IpQuotationOtherChargeExistException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified parameters.
     *
     * @param message the detail message
     * @param cause the cause
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace is writable
     */
    protected IpQuotationOtherChargeExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

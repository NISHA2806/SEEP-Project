package com.seep.exception;

/**
 * Custom checked exception used throughout the SEEP application for
 * business-logic errors (e.g. invalid login, duplicate username,
 * shelter at full capacity, unreachable route, etc.)
 */
public class SEEPException extends Exception {

    public SEEPException(String message) {
        super(message);
    }

    public SEEPException(String message, Throwable cause) {
        super(message, cause);
    }
}

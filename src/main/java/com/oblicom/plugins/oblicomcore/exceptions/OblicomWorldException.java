package com.oblicom.plugins.oblicomcore.exceptions;

/**
 *
 * @author nagib.kanaan
 */
public class OblicomWorldException extends Exception {
    public OblicomWorldException(String message) {
        super(message);
    }

    public OblicomWorldException(String message, Throwable throwable) {
        super(message, throwable);
    }
};
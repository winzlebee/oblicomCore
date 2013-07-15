package com.oblicom.plugins.oblicomcore.exceptions;

/**
 *
 * @author nagib.kanaan
 */
public class OblicomConfigException extends Exception {
    public OblicomConfigException(String message) {
        super(message);
    }

    public OblicomConfigException(String message, Throwable throwable) {
        super(message, throwable);
    }
};

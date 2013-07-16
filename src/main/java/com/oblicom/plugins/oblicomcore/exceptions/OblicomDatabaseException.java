
package com.oblicom.plugins.oblicomcore.exceptions;
/**
 *
 * @author Winfried
 */
public class OblicomDatabaseException extends Exception {
    public OblicomDatabaseException(String message) {
        super(message);
    }

    public OblicomDatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

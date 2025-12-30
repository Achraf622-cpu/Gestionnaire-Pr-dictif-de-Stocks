package com.team.sys_ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when user is not authorized to perform an action.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("Accès non autorisé");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

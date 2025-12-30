package com.team.sys_ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when access to a warehouse is denied.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class EntrepotAccessDeniedException extends RuntimeException {

    private final Long entrepotId;
    private final Long userId;

    public EntrepotAccessDeniedException(Long entrepotId, Long userId) {
        super(String.format("Accès refusé à l'entrepôt %d pour l'utilisateur %d", entrepotId, userId));
        this.entrepotId = entrepotId;
        this.userId = userId;
    }

    public EntrepotAccessDeniedException(String message) {
        super(message);
        this.entrepotId = null;
        this.userId = null;
    }

    public Long getEntrepotId() {
        return entrepotId;
    }

    public Long getUserId() {
        return userId;
    }
}

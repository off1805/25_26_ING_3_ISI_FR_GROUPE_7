package com.projetTransversalIsi.user.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Long id) {
        super("User identified by id < "+id+" > not found.");
    }
}

package com.projetTransversalIsi.user.domain.exceptions;

import com.projetTransversalIsi.common.domain.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String email){
        super("Email <"+email+"> is already use.");
    }
}

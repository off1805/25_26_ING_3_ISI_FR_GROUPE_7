package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.enums.UserStatus;

import java.util.List;

public interface SearchUserUC {
    List<User> execute(String userStatus, String email, String role, boolean deleted);
}

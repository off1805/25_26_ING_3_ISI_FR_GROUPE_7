package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.dto.UserFiltreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchUserUC {
    Page<User> execute(UserFiltreDto command, Pageable page);
}

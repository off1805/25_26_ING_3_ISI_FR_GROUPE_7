package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.dto.UserFiltreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SearchUserUCImpl implements SearchUserUC {

    private final UserRepository userRepository;
    @Override
    public Page<User> execute(UserFiltreDto command, Pageable page){
        return userRepository.findAll(command,page);
    }
}

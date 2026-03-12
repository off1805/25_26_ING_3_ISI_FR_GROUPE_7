package com.projetTransversalIsi.user.domain;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.UserFiltreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
    User registerNewUser(User user);

    void save(User user);

    boolean userAlreadyExists(String email);

    Page<User> findAll(UserFiltreDto command, Pageable page);
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<String> findPasswordMatchEmail(String email);

    List<User> getAllUserOfStaff();

    List<User> search(String userStatus, String email, String role, boolean deleted);

    List<User> findAllDeleted();
    List<User> findDeletedSince(LocalDateTime since);
}

package com.projetTransversalIsi.classe.domain;

import com.projetTransversalIsi.user.domain.User;
import java.util.Set;

public interface classeRepository {
    classe save(classe classe, Set<User> students);
    boolean classeAlreadyExists(String nom);
}

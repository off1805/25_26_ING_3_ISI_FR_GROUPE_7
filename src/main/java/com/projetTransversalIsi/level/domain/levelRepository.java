package com.projetTransversalIsi.level.domain;

public interface levelRepository {
    level save(level level);
    boolean levelAlreadyExists(String nom);
}

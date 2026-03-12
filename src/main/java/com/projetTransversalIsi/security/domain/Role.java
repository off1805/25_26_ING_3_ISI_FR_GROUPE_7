package com.projetTransversalIsi.security.domain;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
public class Role {
    private final String name;
    private Set<Permission> permissions= new HashSet<>();
}

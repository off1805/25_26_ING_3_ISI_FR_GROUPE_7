package com.projetTransversalIsi.level.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "level")
public class JpaLevelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column
    private String description;
}

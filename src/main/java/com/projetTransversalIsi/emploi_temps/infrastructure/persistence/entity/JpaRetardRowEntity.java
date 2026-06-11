package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "retard_row")
public class JpaRetardRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "retard_list_id", nullable = false)
    private Long retardListId;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;
}

package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "presence_row")
public class JpaPresenceRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "presence_list_id", nullable = false)
    private Long presenceListId;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    @Column(name = "present")
    private Boolean present;

    /** true = etudiant arrive en retard (present mais tardif) */
    @Column(name = "retard", nullable = false)
    private boolean retard = false;
}

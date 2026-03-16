package com.projetTransversalIsi.ue.infrastructure;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ue")
public class JpaUeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int credit;

    @Column(nullable = false)
    private int volumeHoraireTotal;

    private String description;
    
    @Column(nullable = false, columnDefinition = "varchar(255) default '#ffffff'")
    private String couleur = "#ffffff";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    public void applyDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}

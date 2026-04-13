package com.projetTransversalIsi.pedagogie.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ue {
    private Long id;
    private String libelle;
    private String code;
    private int credit;
    private int volumeHoraireTotal;
    private String description;

    private Long specialiteId;
    private Set<Long> enseignantIds = new HashSet<>();
    private String couleur = "#ffffff";
    private Integer semestre; // 1 or 2
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private Boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }
}

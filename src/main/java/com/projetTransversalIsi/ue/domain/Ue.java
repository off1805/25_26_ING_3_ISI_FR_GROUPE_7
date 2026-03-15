package com.projetTransversalIsi.ue.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String couleur = "#ffffff";
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

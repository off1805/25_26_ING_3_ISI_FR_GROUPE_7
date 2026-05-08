package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PresenceList {

    private Long id;
    private Long seanceId;
    private Long classeId;
    private Long ueId;
    private Long enseignantId;
    private LocalDate date;
    private LocalDateTime createdAt;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public PresenceList(Long seanceId, Long classeId, Long ueId, Long enseignantId, LocalDate date) {
        this.seanceId = seanceId;
        this.classeId = classeId;
        this.ueId = ueId;
        this.enseignantId = enseignantId;
        this.date = date;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    public void delete() {
        if (this.deleted) throw new IllegalStateException("Liste déjà supprimée");
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}

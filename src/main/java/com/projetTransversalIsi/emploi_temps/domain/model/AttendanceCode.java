package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceCode {

    public enum CodeType { QR, PIN }

    private Long id;
    private Long seanceId;
    private Long enseignantId;
    private CodeType type;
    private String valeur;
    private float heuresAMarquer;
    private int dureeVieMinutes;
    private LocalDateTime createdAt;

    public AttendanceCode(Long seanceId, Long enseignantId, CodeType type,
                          String valeur, float heuresAMarquer, int dureeVieMinutes) {
        this.seanceId = seanceId;
        this.enseignantId = enseignantId;
        this.type = type;
        this.valeur = valeur;
        this.heuresAMarquer = heuresAMarquer;
        this.dureeVieMinutes = dureeVieMinutes;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(dureeVieMinutes));
    }
}

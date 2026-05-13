package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

// Représente un appel (session de pointage) lancé par l'enseignant pour une liste de présence.
// Trois types : MANUEL (enseignant pointe lui-même), PIN (code 6 chiffres), QR (UUID scannable).
// La plage horaire [heureDebut, heureFin] doit être incluse dans celle de la séance associée.
@Getter
@Setter
@NoArgsConstructor
public class Appel {

    public enum TypeAppel { MANUEL, PIN, QR }

    private Long id;
    private Long presenceListId;
    private Long enseignantId;
    private TypeAppel typeAppel;

    // UUID pour QR, 6 chiffres pour PIN, null pour MANUEL.
    private String valeur;

    // Référence vers l'AttendanceCode sous-jacent pour QR/PIN ; null pour MANUEL.
    private Long attendanceCodeId;

    // Doit satisfaire seance.heureDebut <= heureDebut et heureFin <= seance.heureFin.
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Durée de validité du code QR/PIN en minutes ; ignoré pour MANUEL.
    private int dureeVieMinutes;

    private LocalDateTime createdAt;

    public Appel(Long presenceListId, Long enseignantId, TypeAppel typeAppel,
                 String valeur, Long attendanceCodeId,
                 LocalTime heureDebut, LocalTime heureFin, int dureeVieMinutes) {
        this.presenceListId = presenceListId;
        this.enseignantId = enseignantId;
        this.typeAppel = typeAppel;
        this.valeur = valeur;
        this.attendanceCodeId = attendanceCodeId;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.dureeVieMinutes = dureeVieMinutes;
        this.createdAt = LocalDateTime.now();
    }

    // Toujours false pour MANUEL (pas de code à expirer).
    public boolean isExpired() {
        if (typeAppel == TypeAppel.MANUEL || dureeVieMinutes <= 0) return false;
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(dureeVieMinutes));
    }

    public boolean isManuel() {
        return TypeAppel.MANUEL.equals(typeAppel);
    }

    // Appelé lors de la création pour garantir que la plage de l'appel reste dans la séance.
    public void validatePlageHoraire(LocalTime seanceDebut, LocalTime seanceFin) {
        if (heureDebut.isBefore(seanceDebut) || heureFin.isAfter(seanceFin) || !heureDebut.isBefore(heureFin)) {
            throw new IllegalArgumentException(
                "La plage de l'appel [" + heureDebut + "-" + heureFin +
                "] doit être incluse dans la plage de la séance [" + seanceDebut + "-" + seanceFin + "]"
            );
        }
    }
}

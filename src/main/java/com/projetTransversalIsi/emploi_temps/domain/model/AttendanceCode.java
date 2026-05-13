package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Jeton d'authentification de présence généré par l'enseignant pour une séance.
// QR  → valeur = UUID encodé dans une URL de scan (/api/presences/scan?code=<uuid>)
// PIN → valeur = entier 6 chiffres saisi manuellement par l'étudiant.
// La durée de vie (dureeVieMinutes) empêche la réutilisation d'un code après la fin de séance.
@Getter
@Setter
@NoArgsConstructor
public class AttendanceCode {

    public enum CodeType { QR, PIN }

    private Long id;
    private Long seanceId;
    private Long enseignantId;
    private CodeType type;
    // Valeur unique : UUID pour QR (recherche par token dans l'URL), chiffres pour PIN.
    private String valeur;
    // Nombre d'heures inscrit dans PresenceRow.heuresAbsence = 0 quand l'étudiant se marque présent.
    private float heuresAMarquer;
    private int dureeVieMinutes;
    // createdAt capturé à la construction pour calculer l'expiration sans colonne supplémentaire.
    private LocalDateTime createdAt;

    public AttendanceCode(Long seanceId, Long enseignantId, CodeType type,
                          String valeur, float heuresAMarquer, int dureeVieMinutes) {
        this.seanceId = seanceId;
        this.enseignantId = enseignantId;
        this.type = type;
        this.valeur = valeur;
        this.heuresAMarquer = heuresAMarquer;
        this.dureeVieMinutes = dureeVieMinutes;
        // Fixé à la création ; immuable ensuite pour que isExpired() reste déterministe.
        this.createdAt = LocalDateTime.now();
    }

    // Vérifie l'expiration à la volée sans champ booléen persisté,
    // ce qui évite un job planifié de mise à jour de statut.
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(dureeVieMinutes));
    }
}

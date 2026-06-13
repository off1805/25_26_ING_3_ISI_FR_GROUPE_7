package com.projetTransversalIsi.emploi_temps.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Cellule de retard d'un étudiant pour un jour ouvrable donné de la semaine.
// jourSemaine : 1 = Lundi … 6 = Samedi (6 colonnes par RetardRow).
// etudiantId est dénormalisé pour permettre des requêtes directes sans jointure.
//
// Pas d'état "non déterminé" (contrairement à InfoPresenceRow) : il n'y a pas de session
// live à attendre, la case part de false ("à l'heure") et le surveillant la bascule
// d'un clic — sauvegarde immédiate.
@Getter
@Setter
@NoArgsConstructor
public class InfoRetardRow {

    private Long id;
    private Long etudiantId;
    private Long retardRowId;

    private int jourSemaine;
    private LocalDate date;

    private boolean enRetard = false;
    private LocalDateTime markedAt;

    public InfoRetardRow(Long etudiantId, Long retardRowId, int jourSemaine, LocalDate date) {
        this.etudiantId = etudiantId;
        this.retardRowId = retardRowId;
        this.jourSemaine = jourSemaine;
        this.date = date;
        this.enRetard = false;
        this.markedAt = LocalDateTime.now();
    }

    public void toggle() {
        this.enRetard = !this.enRetard;
        this.markedAt = LocalDateTime.now();
    }
}

package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.RetardListResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;
import com.projetTransversalIsi.emploi_temps.domain.model.RetardList;
import com.projetTransversalIsi.emploi_temps.domain.model.RetardRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoRetardRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.RetardListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.RetardRowRepository;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Récupère la feuille de retard hebdomadaire (classe + semaine) ou la crée si elle n'existe
// pas encore : une RetardRow par étudiant inscrit, et 6 InfoRetardRow par ligne (Lundi à
// Samedi, jourSemaine 1..6) initialisées à enRetard=false.
@Component
@RequiredArgsConstructor
public class GetOrCreateRetardListUCImpl implements GetOrCreateRetardListUC {

    private static final int JOURS_OUVRABLES = 6;

    private final RetardListRepository retardListRepo;
    private final RetardRowRepository retardRowRepo;
    private final InfoRetardRowRepository infoRetardRowRepo;
    private final SpringDataStudentProfileRepository studentProfileRepo;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Override
    public RetardListResponseDTO execute(Long classeId, LocalDate semaineDebut) {
        return retardListRepo.findByClasseIdAndSemaineDebutAndDeletedFalse(classeId, semaineDebut)
                .map(RetardListResponseDTO::fromDomain)
                .orElseGet(() -> creer(classeId, semaineDebut));
    }

    private RetardListResponseDTO creer(Long classeId, LocalDate semaineDebut) {
        RetardList nouveau = new RetardList(classeId, semaineDebut);
        anneeScolaireRepository.findActive().map(AnneeScolaire::getId)
                .ifPresent(nouveau::setAnneeScolaireId);
        RetardList retardList = retardListRepo.save(nouveau);

        for (JpaStudentProfileEntity profile : studentProfileRepo.findByClasseId(classeId)) {
            RetardRow row = retardRowRepo.save(new RetardRow(retardList.getId(), profile.getId()));
            for (int jour = 1; jour <= JOURS_OUVRABLES; jour++) {
                infoRetardRowRepo.save(new InfoRetardRow(
                        profile.getId(), row.getId(), jour, semaineDebut.plusDays(jour - 1L)
                ));
            }
        }

        return RetardListResponseDTO.fromDomain(retardList);
    }
}

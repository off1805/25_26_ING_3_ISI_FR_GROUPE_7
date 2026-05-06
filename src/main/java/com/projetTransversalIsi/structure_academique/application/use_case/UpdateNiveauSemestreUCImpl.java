package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataSemestreRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateNiveauSemestreUCImpl implements UpdateNiveauSemestreUC {

    private final SpringDataNiveauRepository niveauRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;
    private final SpringDataSemestreRepository semestreRepository;

    @Override
    public void execute(Long id, Integer semestreActif) {
        if (semestreActif == null || (semestreActif != 1 && semestreActif != 2)) {
            throw new IllegalArgumentException("Le semestre actif doit être 1 ou 2.");
        }
        
        JpaNiveauEntity entity = niveauRepository.findById(id)
                .orElseThrow(() -> new NiveauNotFoundException(id));
                
        entity.setSemestreActif(semestreActif);
        niveauRepository.save(entity);

        // Fetch active academic year
        Optional<AnneeScolaire> activeYearOpt = anneeScolaireRepository.findActive();
        if (activeYearOpt.isPresent()) {
            Long anneeScolaireId = activeYearOpt.get().getId();

            // Set current semester start date
            Optional<JpaSemestreEntity> currentSemestreOpt = semestreRepository
                    .findByAnneeScolaireIdAndNiveauIdAndNumero(anneeScolaireId, id, semestreActif);

            if (currentSemestreOpt.isPresent()) {
                JpaSemestreEntity currentSemestre = currentSemestreOpt.get();
                currentSemestre.setDateDebut(LocalDate.now());
                semestreRepository.save(currentSemestre);
            }

            // If activating Semestre 2, close Semestre 1
            if (semestreActif == 2) {
                Optional<JpaSemestreEntity> previousSemestreOpt = semestreRepository
                        .findByAnneeScolaireIdAndNiveauIdAndNumero(anneeScolaireId, id, 1);
                
                if (previousSemestreOpt.isPresent()) {
                    JpaSemestreEntity previousSemestre = previousSemestreOpt.get();
                    if (previousSemestre.getDateFin() == null) {
                        previousSemestre.setDateFin(LocalDate.now().minusDays(1));
                        semestreRepository.save(previousSemestre);
                    }
                }
            }
        }
    }
}

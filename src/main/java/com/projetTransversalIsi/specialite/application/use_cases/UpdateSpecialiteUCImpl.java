package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.application.dto.UpdateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteNotFoundException;
import com.projetTransversalIsi.Niveau.application.use_cases.FindNiveauByIdUC;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSpecialiteUCImpl implements UpdateSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;
    private final FindNiveauByIdUC findNiveauByIdUC;

    @Transactional
    @Override
    public Specialite execute(Long id, UpdateSpecialiteRequestDTO command) {
        Specialite specialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));

        Niveau niveau = findNiveauByIdUC.execute(command.niveauId());

        specialite.setLibelle(command.libelle());
        specialite.setDescription(command.description());
        specialite.setNiveau(niveau);

        return specialiteRepository.save(specialite);
    }
}

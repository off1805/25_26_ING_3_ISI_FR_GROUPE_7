package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.UpdateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.OffreUeNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateOffreUeUCImpl implements UpdateOffreUeUC {

    private final OffreUeRepository offreUeRepository;

    @Transactional
    @Override
    public OffreUe execute(Long id, UpdateOffreUeRequestDTO command) {
        OffreUe offreUe = offreUeRepository.findById(id)
                .orElseThrow(() -> new OffreUeNotFoundException(id));

        offreUe.setLibelle(command.libelle());
        offreUe.setCode(command.code());
        offreUe.setCredit(command.credit());
        offreUe.setVolumeHoraireTotal(command.volumeHoraireTotal());
        offreUe.setDescription(command.description());
        offreUe.setCouleur(command.couleur());
        offreUe.setSemestre(command.semestre());
        offreUe.setSpecialiteId(command.specialiteId());
        return offreUeRepository.save(offreUe);
    }
}

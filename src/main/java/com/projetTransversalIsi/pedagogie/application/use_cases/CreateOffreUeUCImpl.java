package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.CreateOffreUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.UeRepository;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.OffreUeAlreadyExistsException;
import com.projetTransversalIsi.pedagogie.domain.exceptions.UeNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateOffreUeUCImpl implements CreateOffreUeUC {

    private final OffreUeRepository offreUeRepository;
    private final UeRepository ueRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Transactional
    @Override
    public OffreUe execute(CreateOffreUeRequestDTO command) {

        if (offreUeRepository.offreUeAlreadyExists(command.ueId(), command.anneeScolaireId())) {
            throw new OffreUeAlreadyExistsException(command.ueId(), command.anneeScolaireId());
        }

        Ue ue = ueRepository.findById(command.ueId())
                .orElseThrow(() -> new UeNotFoundException(command.ueId()));

        // L'année scolaire doit exister (validation simple)
        anneeScolaireRepository.findById(command.anneeScolaireId())
                .orElseThrow(() -> new RuntimeException(
                        "Année scolaire introuvable : " + command.anneeScolaireId()));

        // Recopie des champs de base de l'UE
        OffreUe offreUe = new OffreUe();
        offreUe.setUeId(ue.getId());
        offreUe.setAnneeScolaireId(command.anneeScolaireId());
        offreUe.setLibelle(ue.getLibelle());
        offreUe.setCode(ue.getCode());
        offreUe.setCredit(ue.getCredit());
        offreUe.setVolumeHoraireTotal(ue.getVolumeHoraireTotal());
        offreUe.setDescription(ue.getDescription());
        offreUe.setCouleur(ue.getCouleur());
        offreUe.setSpecialiteId(ue.getSpecialiteId());
        offreUe.setEnseignantIds(ue.getEnseignantIds());
        offreUe.setCreatedAt(LocalDateTime.now());

        return offreUeRepository.save(offreUe);
    }
}

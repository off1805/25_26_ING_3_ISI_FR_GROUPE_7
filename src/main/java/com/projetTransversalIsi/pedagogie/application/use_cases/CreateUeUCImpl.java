package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.user.profil.infrastructure.SpringDataTeacherProfileRepository;
import com.projetTransversalIsi.pedagogie.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.domain.UeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.UeAlreadyExistsException;
import com.projetTransversalIsi.pedagogie.domain.exceptions.EnseignantNotFoundException;
import com.projetTransversalIsi.pedagogie.infrastructure.UeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CreateUeUCImpl implements CreateUeUC {

    private final UeRepository ueRepository;
    private final UeMapper ueMapper;
    private final SpringDataTeacherProfileRepository teacherProfileRepository;
    private final OffreUeRepository offreUeRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Transactional
    @Override
    public Ue execute(CreateUeRequestDTO command) {
        if (ueRepository.ueAlreadyExists(command.code())) {
            throw new UeAlreadyExistsException(command.code());
        }

        Set<Long> enseignantIds = command.enseignantIds();
        if (enseignantIds != null) {
            for (Long enseignantId : enseignantIds) {
                if (!teacherProfileRepository.existsById(enseignantId)) {
                    throw new EnseignantNotFoundException(enseignantId);
                }
            }
        }

        Ue ue = ueMapper.toDomain(command);
        ue.setCreatedAt(LocalDateTime.now());
        ue.setIsDeleted(false);

        Ue savedUe = ueRepository.save(ue);

        // Créer automatiquement l'OffreUe liée à l'année scolaire active
        AnneeScolaire activeYear = anneeScolaireRepository.findActive()
                .orElseThrow(() -> new RuntimeException(
                        "Aucune année scolaire active. Activez-en une avant de créer une UE."));

        OffreUe offreUe = new OffreUe();
        offreUe.setUeId(savedUe.getId());
        offreUe.setAnneeScolaireId(activeYear.getId());
        offreUe.setLibelle(savedUe.getLibelle());
        offreUe.setCode(savedUe.getCode());
        offreUe.setCredit(savedUe.getCredit());
        offreUe.setVolumeHoraireTotal(savedUe.getVolumeHoraireTotal());
        offreUe.setDescription(savedUe.getDescription());
        offreUe.setCouleur(savedUe.getCouleur());
        offreUe.setSemestre(savedUe.getSemestre());
        offreUe.setSpecialiteId(savedUe.getSpecialiteId());
        offreUe.setEnseignantIds(savedUe.getEnseignantIds() != null ? savedUe.getEnseignantIds() : new HashSet<>());
        offreUeRepository.save(offreUe);

        return savedUe;
    }
}

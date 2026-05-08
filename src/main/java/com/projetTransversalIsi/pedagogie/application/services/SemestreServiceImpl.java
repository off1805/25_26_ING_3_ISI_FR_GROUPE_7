package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateSemestreRequestDTO;
import com.projetTransversalIsi.pedagogie.application.dto.SemestreResponseDTO;
import com.projetTransversalIsi.pedagogie.application.use_cases.CreateSemestreUC;
import com.projetTransversalIsi.pedagogie.application.use_cases.FindSemestresByAnneeScolaireUC;
import com.projetTransversalIsi.pedagogie.infrastructure.SemestreMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataAnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository.SpringDataSemestreRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SemestreServiceImpl implements SemestreService {

    private final CreateSemestreUC createSemestreUC;
    private final FindSemestresByAnneeScolaireUC findSemestresByAnneeScolaireUC;
    private final SpringDataSemestreRepository semestreRepository;
    private final SpringDataAnneeScolaireRepository anneeScolaireRepository;
    private final SpringDataNiveauRepository niveauRepository;
    private final SemestreMapper semestreMapper;

    @Override
    public SemestreResponseDTO createSemestre(CreateSemestreRequestDTO request) {
        return createSemestreUC.execute(request);
    }

    @Override
    public List<SemestreResponseDTO> getSemestresByAnneeScolaireAndNiveau(Long anneeScolaireId, Long niveauId) {
        return findSemestresByAnneeScolaireUC.execute(anneeScolaireId, niveauId);
    }

    /**
     * Retourne le semestre actif pour un niveau donné, en croisant :
     *  - l'année scolaire active  (AnneeScolaire.active = true)
     *  - le numéro de semestre actif du niveau (Niveau.semestreActif)
     *
     * Logique : classe → specialite → niveau → semestreActif
     *           + anneeScolaire active → findByAnneeScolaireIdAndNiveauIdAndNumero
     */
    @Override
    public Optional<SemestreResponseDTO> getActiveSemestre(Long niveauId) {
        // 1. Année scolaire active
        var anneeScolaire = anneeScolaireRepository.findByActiveTrue().orElse(null);
        if (anneeScolaire == null) return Optional.empty();

        // 2. Niveau → numéro du semestre actif
        var niveau = niveauRepository.findByIdAndDeletedFalse(niveauId).orElse(null);
        if (niveau == null || niveau.getSemestreActif() == null) return Optional.empty();

        // 3. Semestre correspondant à l'année active + niveau + numéro
        return semestreRepository
                .findByAnneeScolaireIdAndNiveauIdAndNumero(
                        anneeScolaire.getId(),
                        niveauId,
                        niveau.getSemestreActif()
                )
                .map(entity -> semestreMapper.toResponseDTO(semestreMapper.toDomain(entity)));
    }
}
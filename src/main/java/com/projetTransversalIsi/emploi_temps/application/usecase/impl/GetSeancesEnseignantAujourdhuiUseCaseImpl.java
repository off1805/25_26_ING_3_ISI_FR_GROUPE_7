package com.projetTransversalIsi.emploi_temps.application.usecase.impl;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesAujourdhuiRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantAujourdhuiUseCase;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du use case {@link GetSeancesEnseignantAujourdhuiUseCase}.
 *
 * <p>Interroge le {@link SeanceRepository} pour récupérer les séances de l'enseignant
 * dont la date est égale à {@link LocalDate#now()}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetSeancesEnseignantAujourdhuiUseCaseImpl
        implements GetSeancesEnseignantAujourdhuiUseCase {

    private final SeanceRepository seanceRepository;

    @Override
    public List<SeanceResponseDTO> execute(GetSeancesAujourdhuiRequestDTO request) {

        if (request.enseignantId() == null) {
            throw new IllegalArgumentException("L'identifiant de l'enseignant est obligatoire");
        }

        LocalDate today = LocalDate.now();
        boolean includeDeleted = Boolean.TRUE.equals(request.includeDeleted());

        log.info("Récupération des séances de l'enseignant {} pour aujourd'hui ({})",
                request.enseignantId(), today);

        return seanceRepository
                .findByEnseignantIdAndDate(request.enseignantId(), today)
                .stream()
                .filter(s -> includeDeleted || !s.isDeleted())
                .sorted((a, b) -> a.getHeureDebut().compareTo(b.getHeureDebut()))
                .map(SeanceResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}

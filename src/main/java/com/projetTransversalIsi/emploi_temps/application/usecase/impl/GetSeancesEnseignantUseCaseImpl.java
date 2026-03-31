package com.projetTransversalIsi.emploi_temps.application.usecase.impl;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesAujourdhuiRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesEnseignantRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesEnseignantRequestDTO.PeriodeType;
import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesSemaineRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantAujourdhuiUseCase;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantParSemaineUseCase;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implémentation du use case générique {@link GetSeancesEnseignantUseCase}.
 *
 * <p>Délègue au use case spécialisé correspondant selon la valeur de
 * {@link GetSeancesEnseignantRequestDTO#periode()} :</p>
 * <ul>
 *   <li>{@link PeriodeType#JOUR}    → {@link GetSeancesEnseignantAujourdhuiUseCase}</li>
 *   <li>{@link PeriodeType#SEMAINE} → {@link GetSeancesEnseignantParSemaineUseCase}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetSeancesEnseignantUseCaseImpl implements GetSeancesEnseignantUseCase {

    private final GetSeancesEnseignantAujourdhuiUseCase jourUseCase;
    private final GetSeancesEnseignantParSemaineUseCase semaineUseCase;

    @Override
    public List<SeanceResponseDTO> execute(GetSeancesEnseignantRequestDTO request) {

        if (request.enseignantId() == null) {
            throw new IllegalArgumentException("L'identifiant de l'enseignant est obligatoire");
        }
        if (request.periode() == null) {
            throw new IllegalArgumentException(
                    "La période est obligatoire (JOUR ou SEMAINE)");
        }

        log.info("Use case générique — enseignant {} / période {}",
                request.enseignantId(), request.periode());

        return switch (request.periode()) {
            case JOUR -> jourUseCase.execute(
                    new GetSeancesAujourdhuiRequestDTO(
                            request.enseignantId(),
                            request.includeDeleted()
                    )
            );
            case SEMAINE -> semaineUseCase.execute(
                    new GetSeancesSemaineRequestDTO(
                            request.enseignantId(),
                            request.dateDeReference(),
                            request.includeDeleted()
                    )
            );
        };
    }
}

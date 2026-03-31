package com.projetTransversalIsi.emploi_temps.application.usecase.impl;

import com.projetTransversalIsi.emploi_temps.application.dto.GetSeancesSemaineRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.usecase.GetSeancesEnseignantParSemaineUseCase;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du use case {@link GetSeancesEnseignantParSemaineUseCase}.
 *
 * <p>Calcule le lundi et le dimanche de la semaine ISO contenant
 * {@code request.dateDeReference()} (ou la semaine courante si null),
 * puis délègue la requête au {@link SeanceRepository}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetSeancesEnseignantParSemaineUseCaseImpl
        implements GetSeancesEnseignantParSemaineUseCase {

    private final SeanceRepository seanceRepository;

    @Override
    public List<SeanceResponseDTO> execute(GetSeancesSemaineRequestDTO request) {

        if (request.enseignantId() == null) {
            throw new IllegalArgumentException("L'identifiant de l'enseignant est obligatoire");
        }

        LocalDate reference = request.dateDeReference() != null
                ? request.dateDeReference()
                : LocalDate.now();

        LocalDate lundi    = reference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate dimanche = reference.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        boolean includeDeleted = Boolean.TRUE.equals(request.includeDeleted());

        log.info("Récupération des séances de l'enseignant {} du {} au {}",
                request.enseignantId(), lundi, dimanche);

        return seanceRepository
                .findByEnseignantIdAndDateBetween(request.enseignantId(), lundi, dimanche)
                .stream()
                .filter(s -> includeDeleted || !s.isDeleted())
                .sorted(Comparator
                        .comparing(s -> s.getDateSeance().atTime(s.getHeureDebut()))
                )
                .map(SeanceResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}

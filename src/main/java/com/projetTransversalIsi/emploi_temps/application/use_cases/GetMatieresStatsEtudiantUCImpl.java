package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.MatiereStatsEtudiantDTO;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataPresenceStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetMatieresStatsEtudiantUCImpl implements GetMatieresStatsEtudiantUC {

    private final SpringDataPresenceStatsRepository statsRepository;

    @Override
    public List<MatiereStatsEtudiantDTO> execute(Long etudiantId, Long classeId, Long specialiteId) {
        return statsRepository
                .findMatiereStatsEtudiant(etudiantId, classeId, specialiteId)
                .stream()
                .map(MatiereStatsEtudiantDTO::from)
                .toList();
    }
}

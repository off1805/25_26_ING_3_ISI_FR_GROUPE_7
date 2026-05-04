package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.application.dto.OffreUeFiltreDto;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchOffreUeUCImpl implements SearchOffreUeUC {

    private final OffreUeRepository offreUeRepository;
    private final AnneeScolaireRepository anneeScolaireRepository;

    @Override
    public Page<OffreUe> execute(Long anneeScolaireId, Long ueId, Pageable pageable) {
        if (anneeScolaireId != null) {
            return offreUeRepository.findByAnneeScolaireId(anneeScolaireId, pageable);
        }
        if (ueId != null) {
            return offreUeRepository.findByUeId(ueId, pageable);
        }
        return offreUeRepository.findAll(pageable);
    }

    @Override
    public Page<OffreUe> executeBySpecialiteActiveYear(OffreUeFiltreDto filtre, Pageable pageable) {
        if (filtre.getSpecialiteId() == null) {
            throw new IllegalArgumentException("specialiteId est requis");
        }
        AnneeScolaire activeYear = anneeScolaireRepository.findActive()
                .orElseThrow(() -> new RuntimeException("Aucune année scolaire active. Veuillez en activer une."));
        return offreUeRepository.findBySpecialiteIdAndAnneeScolaireId(
                filtre.getSpecialiteId(), activeYear.getId(), filtre, pageable);
    }
}

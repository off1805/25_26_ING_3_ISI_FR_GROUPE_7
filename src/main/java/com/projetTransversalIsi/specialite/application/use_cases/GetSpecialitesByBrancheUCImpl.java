package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetSpecialitesByBrancheUCImpl implements GetSpecialitesByBrancheUC {

    private final SpecialiteRepository specialiteRepository;

    /**
     * Retourne les spécialités disponibles pour une branche donnée (ISI ou SRT par exemple).
     * Si un niveau est fourni, filtre uniquement les spécialités accessibles à ce niveau.
     * Bon c'est pour eviter les abus et erreur
     */
    @Override
    public List<Specialite> execute(String brancheCode, Integer niveau) {
        if (niveau != null) {
            return specialiteRepository.findByBrancheCodeAndNiveauMinimumLessThanEqual(
                    brancheCode.toUpperCase(), niveau);
        }
        return specialiteRepository.findByBrancheCode(brancheCode.toUpperCase());
    }
}

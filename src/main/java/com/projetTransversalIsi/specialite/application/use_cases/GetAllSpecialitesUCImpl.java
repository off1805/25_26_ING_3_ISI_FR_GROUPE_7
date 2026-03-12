package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllSpecialitesUCImpl implements GetAllSpecialitesUC {

    private final SpecialiteRepository specialiteRepository;

    @Override
    public List<Specialite> execute() {
        return specialiteRepository.findAllActive();
    }
}

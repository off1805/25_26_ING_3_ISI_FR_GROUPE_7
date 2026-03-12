package com.projetTransversalIsi.specialite.application.services;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecialiteService implements GetSpecialiteByCode {

    private final SpecialiteRepository specialiteRepository;

    @Override
    public Optional<Specialite> getByCode(String code) {
        return specialiteRepository.findByCode(code);
    }
}

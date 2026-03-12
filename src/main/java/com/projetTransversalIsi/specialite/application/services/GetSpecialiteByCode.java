package com.projetTransversalIsi.specialite.application.services;

import com.projetTransversalIsi.specialite.domain.Specialite;

import java.util.Optional;


public interface GetSpecialiteByCode {
    Optional<Specialite> getByCode(String code);
}

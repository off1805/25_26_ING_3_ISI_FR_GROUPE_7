package com.projetTransversalIsi.justificatif.domain.repository;

import com.projetTransversalIsi.justificatif.domain.model.Justificatif;

import java.util.List;
import java.util.Optional;

public interface JustificatifRepository {
    Justificatif save(Justificatif justificatif);
    Optional<Justificatif> findById(Long id);
    List<Justificatif> findByEtudiantId(Long etudiantId);
    // Retourne TOUS les justificatifs de l'étudiant qui référencent cette séance.
    List<Justificatif> findByEtudiantIdAndSeanceId(Long etudiantId, Long seanceId);
    List<Justificatif> findAll();
    void delete(Long id);
}

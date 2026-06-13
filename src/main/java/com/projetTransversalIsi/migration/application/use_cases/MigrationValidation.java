package com.projetTransversalIsi.migration.application.use_cases;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;

final class MigrationValidation {

    private MigrationValidation() {}

    static void checkFiliereCompatibility(JpaClasseEntity classeSource, JpaClasseEntity classeDestination) {
        JpaFiliereEntity filiereSource = classeSource.getSpecialite().getNiveau().getFiliere();
        JpaFiliereEntity filiereDestination = classeDestination.getSpecialite().getNiveau().getFiliere();

        if (filiereSource.getId().equals(filiereDestination.getId())) {
            return;
        }

        if (filiereSource.isTroncCommun()
                && filiereSource.getCycle().getId().equals(filiereDestination.getCycle().getId())) {
            return;
        }

        throw new IllegalArgumentException("Migration impossible : la filière de destination n'est pas compatible avec la filière d'origine.");
    }
}

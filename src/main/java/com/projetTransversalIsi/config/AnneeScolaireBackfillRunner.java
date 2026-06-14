package com.projetTransversalIsi.config;

import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Comble la colonne annee_scolaire_id (NULL) des lignes emploi_temps/presence_list/retard_list
// créées avant l'introduction de ce champ, en les rattachant à l'année active. Idempotent :
// plus aucune ligne NULL après le premier démarrage, donc no-op ensuite.
@Component
@RequiredArgsConstructor
@Slf4j
public class AnneeScolaireBackfillRunner implements ApplicationRunner {

    private final AnneeScolaireRepository anneeScolaireRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        anneeScolaireRepository.findActive().map(AnneeScolaire::getId).ifPresent(activeId -> {
            int emploiTemps = jdbcTemplate.update(
                    "UPDATE emploi_temps SET annee_scolaire_id = ? WHERE annee_scolaire_id IS NULL", activeId);
            int presenceList = jdbcTemplate.update(
                    "UPDATE presence_list SET annee_scolaire_id = ? WHERE annee_scolaire_id IS NULL", activeId);
            int retardList = jdbcTemplate.update(
                    "UPDATE retard_list SET annee_scolaire_id = ? WHERE annee_scolaire_id IS NULL", activeId);
            int seance = jdbcTemplate.update(
                    "UPDATE seance SET annee_scolaire_id = ? WHERE annee_scolaire_id IS NULL", activeId);
            int justificatif = jdbcTemplate.update(
                    "UPDATE justificatif SET annee_scolaire_id = ? WHERE annee_scolaire_id IS NULL", activeId);

            if (emploiTemps > 0 || presenceList > 0 || retardList > 0 || seance > 0 || justificatif > 0) {
                log.info("Backfill annee_scolaire_id={} : emploi_temps={}, presence_list={}, retard_list={}, seance={}, justificatif={}",
                        activeId, emploiTemps, presenceList, retardList, seance, justificatif);
            }
        });
    }
}

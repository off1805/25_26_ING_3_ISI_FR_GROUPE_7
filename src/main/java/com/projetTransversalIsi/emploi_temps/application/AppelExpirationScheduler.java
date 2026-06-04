package com.projetTransversalIsi.emploi_temps.application;

import com.projetTransversalIsi.emploi_temps.application.use_cases.CloseAppelUC;
import com.projetTransversalIsi.emploi_temps.domain.model.Appel;
import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppelExpirationScheduler {

    private final TaskScheduler taskScheduler;
    private final CloseAppelUC closeAppelUC;
    private final AppelRepository appelRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;

    /**
     * Appeler juste après la sauvegarde d'un appel QR ou PIN.
     * Programme une tâche unique qui se déclenchera exactement à l'expiration du code.
     */
    public void scheduleClose(Appel appel) {
        if (appel.isManuel() || appel.getDureeVieMinutes() <= 0) return;

        Instant expiresAt = appel.getCreatedAt()
                .plusMinutes(appel.getDureeVieMinutes())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        taskScheduler.schedule(
                () -> closeAppelUC.execute(appel.getId()),
                expiresAt
        );
    }

    /**
     * Au démarrage du serveur, récupère les appels expirés pendant l'arrêt
     * (rows isPresent=null dont le code a déjà expiré) et les clôt immédiatement.
     * Re-programme ceux qui sont encore valides mais dont le timer a été perdu.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void recoverOnStartup() {
        List<Long> pendingAppelIds = infoPresenceRowRepo.findDistinctAppelIdsWithPendingRows();

        for (Long appelId : pendingAppelIds) {
            appelRepo.findById(appelId).ifPresent(appel -> {
                if (appel.isManuel()) return;

                if (appel.isExpired()) {
                    closeAppelUC.execute(appel.getId());
                } else {
                    scheduleClose(appel);
                }
            });
        }
    }
}

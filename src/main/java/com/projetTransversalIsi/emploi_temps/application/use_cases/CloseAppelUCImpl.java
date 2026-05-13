package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CloseAppelUCImpl implements CloseAppelUC {

    private final InfoPresenceRowRepository infoPresenceRowRepo;
    private final PresenceRowRepository presenceRowRepo;

    // Clôture un appel : passe les InfoPresenceRow isPresent=null à false,
    // puis recalcule present sur chaque PresenceRow concernée.
    @Override
    public int execute(Long appelId) {
        List<InfoPresenceRow> pending = infoPresenceRowRepo.findByAppelIdAndIsPresentIsNull(appelId);

        for (InfoPresenceRow info : pending) {
            info.setIsPresent(false);
            infoPresenceRowRepo.save(info);

            // Recalcule le statut global de la PresenceRow depuis toutes ses InfoPresenceRow.
            presenceRowRepo.findById(info.getPresenceRowId()).ifPresent(presenceRow -> {
                presenceRow.recalculatePresent(
                        infoPresenceRowRepo.findByPresenceRowId(presenceRow.getId()));
                presenceRowRepo.save(presenceRow);
            });
        }

        return pending.size();
    }
}

package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CloseAllPendingAppelsUCImpl implements CloseAllPendingAppelsUC {

    private final PresenceRowRepository presenceRowRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;
    private final CloseAppelUC closeAppelUC;

    @Override
    public int execute(Long presenceListId) {
        List<Long> rowIds = presenceRowRepo.findByPresenceListId(presenceListId).stream()
                .map(PresenceRow::getId)
                .toList();

        if (rowIds.isEmpty()) {
            return 0;
        }

        Set<Long> pendingAppelIds = infoPresenceRowRepo.findByPresenceRowIdIn(rowIds).stream()
                .filter(info -> info.getIsPresent() == null)
                .map(InfoPresenceRow::getAppelId)
                .collect(Collectors.toSet());

        int total = 0;
        for (Long appelId : pendingAppelIds) {
            total += closeAppelUC.execute(appelId);
        }
        return total;
    }
}

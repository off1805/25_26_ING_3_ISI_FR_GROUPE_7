package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletePresenceListUCImpl implements DeletePresenceListUC {

    private final PresenceListRepository presenceListRepo;

    @Override
    public void execute(Long id) {
        PresenceList presenceList = presenceListRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Liste de présence introuvable : " + id));
        presenceList.delete();
        presenceListRepo.delete(presenceList);
    }
}

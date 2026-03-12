package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import com.projetTransversalIsi.ue.domain.exceptions.UeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteUeUCImpl implements DeleteUeUC {

    private final UeRepository ueRepository;

    @Transactional
    @Override
    public void execute(Long id) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new UeNotFoundException(id));

        if (Boolean.TRUE.equals(ue.getIsDeleted())) {
            throw new IllegalStateException("La UE " + id + " est déjà supprimée.");
        }

        ue.delete();
        ueRepository.delete(ue);
    }
}

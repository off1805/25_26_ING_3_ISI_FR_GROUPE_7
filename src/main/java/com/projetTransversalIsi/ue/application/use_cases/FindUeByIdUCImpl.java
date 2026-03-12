package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import com.projetTransversalIsi.ue.domain.exceptions.UeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindUeByIdUCImpl implements FindUeByIdUC {

    private final UeRepository ueRepository;

    @Override
    public Ue execute(Long id) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new UeNotFoundException(id));
        if (ue.getIsDeleted()) {
            throw new UeNotFoundException(id);
        }
        return ue;
    }
}

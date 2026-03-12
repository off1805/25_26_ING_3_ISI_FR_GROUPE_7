package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import com.projetTransversalIsi.ue.domain.exceptions.UeNotFoundException;
import com.projetTransversalIsi.ue.application.dto.UpdateUeRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUeUCImpl implements UpdateUeUC {

    private final UeRepository ueRepository;
    private final com.projetTransversalIsi.ue.infrastructure.UeMapper ueMapper;

    @Transactional
    @Override
    public Ue execute(Long id, UpdateUeRequestDTO command) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new UeNotFoundException(id));

        ueMapper.updateFromDTO(command, ue);

        return ueRepository.save(ue);
    }
}

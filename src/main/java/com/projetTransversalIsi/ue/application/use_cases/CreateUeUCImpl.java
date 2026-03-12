package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.domain.UeRepository;
import com.projetTransversalIsi.ue.domain.exceptions.UeAlreadyExistsException;
import com.projetTransversalIsi.ue.application.dto.CreateUeRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateUeUCImpl implements CreateUeUC {

    private final UeRepository ueRepository;
    private final com.projetTransversalIsi.ue.infrastructure.UeMapper ueMapper;

    @Transactional
    @Override
    public Ue execute(CreateUeRequestDTO command) {
        if (ueRepository.ueAlreadyExists(command.code())) {
            throw new UeAlreadyExistsException(command.code());
        }

        Ue ue = ueMapper.toDomain(command);
        ue.setCreatedAt(LocalDateTime.now());
        ue.setIsDeleted(false);

        return ueRepository.save(ue);
    }
}

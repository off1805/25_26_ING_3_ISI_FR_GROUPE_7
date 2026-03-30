package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.profil.infrastructure.SpringDataTeacherProfileRepository;
import com.projetTransversalIsi.pedagogie.application.dto.CreateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.domain.UeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.UeAlreadyExistsException;
import com.projetTransversalIsi.pedagogie.domain.exceptions.EnseignantNotFoundException;
import com.projetTransversalIsi.pedagogie.infrastructure.UeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CreateUeUCImpl implements CreateUeUC {

    private final UeRepository ueRepository;
    private final UeMapper ueMapper;
    private final SpringDataTeacherProfileRepository teacherProfileRepository;

    @Transactional
    @Override
    public Ue execute(CreateUeRequestDTO command) {
        if (ueRepository.ueAlreadyExists(command.code())) {
            throw new UeAlreadyExistsException(command.code());
        }

        Set<Long> enseignantIds = command.enseignantIds();
        if (enseignantIds != null) {
            for (Long enseignantId : enseignantIds) {
                if (!teacherProfileRepository.existsById(enseignantId)) {
                    throw new EnseignantNotFoundException(enseignantId);
                }
            }
        }

        Ue ue = ueMapper.toDomain(command);
        ue.setCreatedAt(LocalDateTime.now());
        ue.setIsDeleted(false);

        return ueRepository.save(ue);
    }
}

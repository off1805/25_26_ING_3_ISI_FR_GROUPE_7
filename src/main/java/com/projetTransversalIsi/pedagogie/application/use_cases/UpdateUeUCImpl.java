package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.profil.infrastructure.SpringDataTeacherProfileRepository;
import com.projetTransversalIsi.pedagogie.application.dto.UpdateUeRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.domain.UeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.EnseignantNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.exceptions.UeNotFoundException;
import com.projetTransversalIsi.pedagogie.infrastructure.UeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UpdateUeUCImpl implements UpdateUeUC {

    private final UeRepository ueRepository;
    private final UeMapper ueMapper;
    private final SpringDataTeacherProfileRepository teacherProfileRepository;

    @Transactional
    @Override
    public Ue execute(Long id, UpdateUeRequestDTO command) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new UeNotFoundException(id));

        Set<Long> enseignantIds = command.enseignantIds();
        if (enseignantIds != null) {
            for (Long enseignantId : enseignantIds) {
                if (!teacherProfileRepository.existsById(enseignantId)) {
                    throw new EnseignantNotFoundException(enseignantId);
                }
            }
        }

        ueMapper.updateFromDTO(command, ue);

        return ueRepository.save(ue);
    }
}

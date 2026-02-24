package com.projetTransversalIsi.classe.application.usecase;


import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.classe;
import com.projetTransversalIsi.classe.domain.classeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClassUCImpl implements CreateClassUC {

    private final classeRepository classerepo;
    private final FindUserByIdsAccessPort getStudentsByIds;

    @Transactional
    @Override
    public classe execute(CreateClassRequestDTO command) {
        return null;
    }

    Set<>

}

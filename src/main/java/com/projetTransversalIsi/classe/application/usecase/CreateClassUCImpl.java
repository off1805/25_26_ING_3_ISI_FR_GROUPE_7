package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.ClasseRepository;
import com.projetTransversalIsi.specialite.application.use_cases.FindSpecialiteByIdUC;
import com.projetTransversalIsi.specialite.domain.Specialite;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClassUCImpl implements CreateClassUC {

    private final ClasseRepository classerepo;
    private final FindSpecialiteByIdUC findSpecialiteByIdUC;

    @Transactional
    @Override
    public Classe execute(CreateClassRequestDTO command) {
        Specialite specialite = findSpecialiteByIdUC.execute(command.specialiteId());
        
        Classe classe = new Classe(
                command.code().toUpperCase(),
                command.description(),
                specialite
        );

        return classerepo.save(classe);
    }
}

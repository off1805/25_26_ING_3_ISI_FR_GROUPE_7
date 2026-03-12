package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.application.dto.UpdateClasseRequestDTO;
import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.ClasseRepository;
import com.projetTransversalIsi.classe.domain.exceptions.ClasseNotFoundException;
import com.projetTransversalIsi.specialite.application.use_cases.FindSpecialiteByIdUC;
import com.projetTransversalIsi.specialite.domain.Specialite;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateClasseUCImpl implements UpdateClasseUC {

    private final ClasseRepository repository;
    private final FindSpecialiteByIdUC findSpecialiteByIdUC;

    @Transactional
    @Override
    public Classe execute(Long id, UpdateClasseRequestDTO command) {
        Classe classe = repository.findById(id)
                .orElseThrow(() -> new ClasseNotFoundException(id));

        Specialite specialite = findSpecialiteByIdUC.execute(command.specialiteId());

        classe.update(
                command.code().toUpperCase(),
                command.description(),
                specialite
        );

        return repository.save(classe);
    }
}

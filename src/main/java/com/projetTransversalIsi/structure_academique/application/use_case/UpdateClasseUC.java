package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateClasseRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.ClasseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateClasseUC {

    private final ClasseRepository repository;

    @Transactional
    public Classe execute(Long id, UpdateClasseRequestDTO command) {
        Classe classe = repository.findById(id)
                .orElseThrow(() -> new ClasseNotFoundException(id));

        classe.update(
                command.code().toUpperCase(),
                command.description()
        );

        return repository.save(classe);
    }
}

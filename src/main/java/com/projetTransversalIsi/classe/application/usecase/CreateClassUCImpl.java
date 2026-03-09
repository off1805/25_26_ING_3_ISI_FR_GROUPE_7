package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.classe;
import com.projetTransversalIsi.classe.domain.classeRepository;
import com.projetTransversalIsi.classe.domain.exception.ClasseAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CreateClassUCImpl implements CreateClassUC {

    private final classeRepository classerepo;

    @SneakyThrows
    @Transactional
    @Override
    public classe execute(CreateClassRequestDTO command) {

        if(classerepo.classeAlreadyExists(command.Classname())) {
           throw new Throwable(command.Classname());
        }

        classe newClasse = new classe();
        newClasse.setNom(command.Classname());
        newClasse.setEffectif(0);  // Pour l'instant, 0 étudiants

        return classerepo.save(newClasse, Set.of());  // Set vide pour les étudiants
    }
}

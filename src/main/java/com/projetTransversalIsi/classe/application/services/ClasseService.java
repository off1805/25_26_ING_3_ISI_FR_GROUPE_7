package com.projetTransversalIsi.classe.application.services;

import com.projetTransversalIsi.classe.application.dto.*;
import com.projetTransversalIsi.classe.application.usecase.*;
import com.projetTransversalIsi.classe.domain.Classe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClasseService implements DefaultClasseService {

    private final CreateClassUC createClassUC;
    private final UpdateClasseUC updateClasseUC;
    private final DeleteClasseUC deleteClasseUC;
    private final FindClasseByIdUC findClasseByIdUC;
    private final FindAllClassesUC findAllClassesUC;
    private final FindClassesBySpecialiteIdUC findClassesBySpecialiteIdUC;

    @Override
    public ClasseResponseDTO createClasse(CreateClassRequestDTO request) {
        Classe classe = createClassUC.execute(request);
        return ClasseResponseDTO.fromDomain(classe);
    }

    @Override
    public ClasseResponseDTO updateClasse(Long id, UpdateClasseRequestDTO request) {
        return ClasseResponseDTO.fromDomain(updateClasseUC.execute(id, request));
    }

    @Override
    public void deleteClasse(Long id) {
        deleteClasseUC.execute(id);
    }

    @Override
    public ClasseResponseDTO getClasseById(Long id) {
        Classe classe = findClasseByIdUC.execute(id);
        return ClasseResponseDTO.fromDomain(classe);
    }

    @Override
    public List<ClasseResponseDTO> getAllClasses() {
        return findAllClassesUC.execute().stream()
                .map(ClasseResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClasseResponseDTO> getClassesBySpecialiteId(Long specialiteId) {
        return findClassesBySpecialiteIdUC.execute(specialiteId).stream()
                .map(ClasseResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}

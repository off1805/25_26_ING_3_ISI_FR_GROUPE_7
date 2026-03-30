package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.use_case.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClasseService {

    private final CreateClassUC createClassUC;
    private final UpdateClasseUC updateClasseUC;
    private final DeleteClasseUC deleteClasseUC;
    private final FindClasseByIdUC findClasseByIdUC;
    private final FindAllClassesUC findAllClassesUC;
    private final FindClassesBySpecialiteIdUC findClassesBySpecialiteIdUC;

    public ClasseResponseDTO createClasse(CreateClassRequestDTO request) {
        return ClasseResponseDTO.fromDomain(createClassUC.execute(request));
    }

    public ClasseResponseDTO updateClasse(Long id, UpdateClasseRequestDTO request) {
        return ClasseResponseDTO.fromDomain(updateClasseUC.execute(id, request));
    }

    public void deleteClasse(Long id) {
        deleteClasseUC.execute(id);
    }

    public ClasseResponseDTO getClasseById(Long id) {
        return ClasseResponseDTO.fromDomain(findClasseByIdUC.execute(id));
    }

    public List<ClasseResponseDTO> getAllClasses() {
        return findAllClassesUC.execute().stream()
                .map(ClasseResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public List<ClasseResponseDTO> getClassesBySpecialiteId(Long specialiteId) {
        return findClassesBySpecialiteIdUC.execute(specialiteId).stream()
                .map(ClasseResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}

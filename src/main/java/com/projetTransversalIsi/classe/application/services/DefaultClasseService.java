package com.projetTransversalIsi.classe.application.services;

import com.projetTransversalIsi.classe.application.dto.*;
import java.util.List;

public interface DefaultClasseService {
    ClasseResponseDTO createClasse(CreateClassRequestDTO request);
    ClasseResponseDTO updateClasse(Long id, UpdateClasseRequestDTO request);
    void deleteClasse(Long id);
    ClasseResponseDTO getClasseById(Long id);
    List<ClasseResponseDTO> getAllClasses();
    List<ClasseResponseDTO> getClassesBySpecialiteId(Long specialiteId);
}

package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.CreateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.application.dto.SchoolResponseDTO;
import com.projetTransversalIsi.structure_academique.application.dto.UpdateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.application.use_case.CreateSchoolUC;
import com.projetTransversalIsi.structure_academique.application.use_case.FindSchoolByIdUC;
import com.projetTransversalIsi.structure_academique.application.use_case.UpdateSchoolUC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final CreateSchoolUC createSchoolUC;
    private final UpdateSchoolUC updateSchoolUC;
    private final FindSchoolByIdUC findSchoolByIdUC;

    public SchoolResponseDTO createSchool(CreateSchoolRequestDTO request) {
        return SchoolResponseDTO.fromDomain(createSchoolUC.execute(request));
    }

    public SchoolResponseDTO updateSchool(Long id, UpdateSchoolRequestDTO request) {
        return SchoolResponseDTO.fromDomain(updateSchoolUC.execute(id, request));
    }

    public SchoolResponseDTO getSchoolById(Long id) {
        return SchoolResponseDTO.fromDomain(findSchoolByIdUC.execute(id));
    }
}

package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.use_case.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CycleService {

    private final CreateCycleUC createCycleUC;
    private final UpdateCycleUC updateCycleUC;
    private final DeleteCycleUC deleteCycleUC;
    private final FindCycleByIdUC findCycleByIdUC;
    private final GetAllCyclesUC getAllCyclesUC;
    private final ModifyCycleStatusUC modifyCycleStatusUC;
    private final LinkCycleToSchoolUC linkCycleToSchoolUC;

    public CycleResponseDTO createCycle(CreateCycleRequestDTO request) {
        return CycleResponseDTO.fromDomain(createCycleUC.execute(request));
    }

    public CycleResponseDTO updateCycle(Long id, UpdateCycleRequestDTO request) {
        return CycleResponseDTO.fromDomain(updateCycleUC.execute(id, request));
    }

    public void deleteCycle(Long id) {
        deleteCycleUC.execute(id);
    }

    public CycleResponseDTO getCycleById(Long id) {
        return CycleResponseDTO.fromDomain(findCycleByIdUC.execute(id));
    }

    public List<CycleResponseDTO> getAllCycles() {
        return getAllCyclesUC.execute().stream()
                .map(CycleResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public CycleResponseDTO modifyStatus(Long id, ModifyCycleStatusDTO statusDTO) {
        return CycleResponseDTO.fromDomain(modifyCycleStatusUC.execute(id, statusDTO));
    }

    public CycleResponseDTO linkCycleToSchool(Long cycleId, Long schoolId) {
        return CycleResponseDTO.fromDomain(linkCycleToSchoolUC.execute(cycleId, schoolId));
    }
}

package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.exception.SchoolNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.model.School;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindSchoolByIdUC {

    private final SchoolRepository schoolRepository;

    public School execute(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolNotFoundException(id));
    }
}

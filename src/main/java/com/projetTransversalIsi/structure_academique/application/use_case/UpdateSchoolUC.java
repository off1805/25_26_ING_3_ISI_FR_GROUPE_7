package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.exception.SchoolNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.model.School;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSchoolUC {

    private final SchoolRepository schoolRepository;

    @Transactional
    public School execute(Long id, UpdateSchoolRequestDTO request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolNotFoundException(id));
        school.setName(request.name());
        school.setLatitude(request.latitude());
        school.setLongitude(request.longitude());
        school.setRayon(request.rayon());
        return schoolRepository.save(school);
    }
}

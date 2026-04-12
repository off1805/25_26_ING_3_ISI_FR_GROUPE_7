package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateSchoolRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.School;
import com.projetTransversalIsi.structure_academique.domain.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSchoolUC {

    private final SchoolRepository schoolRepository;

    @Transactional
    public School execute(CreateSchoolRequestDTO request) {
        School school = new School();
        school.setName(request.name());
        school.setLatitude(request.latitude());
        school.setLongitude(request.longitude());
        school.setRayon(request.rayon());
        return schoolRepository.save(school);
    }
}

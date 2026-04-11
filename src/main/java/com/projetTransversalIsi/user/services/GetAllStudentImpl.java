package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.student.application.use_cases.JpaStudentSpec;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;
import com.projetTransversalIsi.student.application.dto.FilterStudent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllStudentImpl implements GetAllStudent {
    private final SpringDataStudentProfileRepository repo;
    @Override
    public  Page<EnrollStudentResponseDTO> execute(FilterStudent comand, Pageable page){
        Specification<JpaStudentProfileEntity> spec= Specification
                .where(JpaStudentSpec.isInClass(comand.getClasseId()));


       return  repo.findAll(spec,page).map(e->{
          return EnrollStudentResponseDTO.builder()
                   .nom(e.getNom())
                   .classeId(e.getClasse().getId())
                   .userId(e.getUser().getId())
                   .prenom(e.getPrenom())
                   .matricule(e.getMatricule())
                   .build();
       });
    }
}

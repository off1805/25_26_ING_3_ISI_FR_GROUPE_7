package com.projetTransversalIsi.user.services;

import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;
import com.projetTransversalIsi.student.application.dto.FilterStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllStudent {
    Page<EnrollStudentResponseDTO> execute(FilterStudent comand, Pageable page);
}

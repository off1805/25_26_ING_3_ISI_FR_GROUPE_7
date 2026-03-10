package com.projetTransversalIsi.classe.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateClassRequestDTO(
        @NotBlank String Classname,
        @NotBlank @Size(min =  8) String idClass,
        Set<String> idStudents
) {
}

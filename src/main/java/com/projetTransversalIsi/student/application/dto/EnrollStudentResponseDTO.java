package com.projetTransversalIsi.student.application.dto;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class EnrollStudentResponseDTO
        {
           private Long userId;
           private String nom;
           private String prenom;
           private  String matricule;
           private Long classeId;

}
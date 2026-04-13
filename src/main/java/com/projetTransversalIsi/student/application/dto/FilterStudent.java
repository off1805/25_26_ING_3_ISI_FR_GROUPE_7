package com.projetTransversalIsi.student.application.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class FilterStudent {
   private String email;
   private String nom;
   private String prenom;
   private String matricule;
   private Long classeId;

}

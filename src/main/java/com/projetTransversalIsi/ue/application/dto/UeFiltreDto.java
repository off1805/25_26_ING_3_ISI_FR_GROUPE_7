package com.projetTransversalIsi.ue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UeFiltreDto {
    private String libelle;
    private String code;
    private Long specialiteId;
    private Long enseignantId;
    private Boolean deleted = false;
}

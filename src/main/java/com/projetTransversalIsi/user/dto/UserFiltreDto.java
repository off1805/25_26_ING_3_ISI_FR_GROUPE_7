package com.projetTransversalIsi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserFiltreDto {
    private String status;
    private List<String> role;
    private Boolean deleted = false;
    private String email;
}

package com.projetTransversalIsi.level.infrastructure;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JPAlevelEntity {

@id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=false)
    private  String nom;

}

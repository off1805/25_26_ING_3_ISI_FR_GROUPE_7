package com.projetTransversalIsi.school.infrastructure;


import jakarta.persistence.*;
import jakarta.persistence.SecondaryTable;
import lombok.Data;

@Data
@Entity
public class JpaSchoolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double rayon;

    private String name;

}

package com.projetTransversalIsi.cours.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class JpaCoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date coursStart ;

    @Column(nullable = false)
    private Date coursEnd ;
}

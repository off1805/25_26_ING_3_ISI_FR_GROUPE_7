package com.projetTransversalIsi.user.profil.infrastructure;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="student_profile")
public class JpaStudentProfileEntity extends JpaProfileEntity {

    public JpaStudentProfileEntity() {}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private JpaClasseEntity classe;
}

package com.projetTransversalIsi.profil.infrastructure;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="student_profile")
public class JpaStudentProfileEntity extends JpaProfileEntity{

    public JpaStudentProfileEntity(){}

}

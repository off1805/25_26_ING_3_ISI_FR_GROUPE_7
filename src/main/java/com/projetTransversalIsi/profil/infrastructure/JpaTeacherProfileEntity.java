package com.projetTransversalIsi.profil.infrastructure;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="teacher_profile")
public class JpaTeacherProfileEntity extends JpaProfileEntity{
    public JpaTeacherProfileEntity(){}
}

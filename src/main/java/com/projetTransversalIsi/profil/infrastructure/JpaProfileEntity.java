package com.projetTransversalIsi.profil.infrastructure;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "profile")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class JpaProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile")
    private JpaUserEntity user;

    @Column(nullable = true)
    private String matricule;
}

package com.projetTransversalIsi.classe.infrastructure;


import com.projetTransversalIsi.specialite.infrastructure.JpaSpecialiteEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "classe")
public class JpaClasseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id", nullable = false)
    private JpaSpecialiteEntity specialite;

    @PrePersist
    public void prePersist() {
        if (code != null) {
            code = code.toUpperCase();
        }
    }
}

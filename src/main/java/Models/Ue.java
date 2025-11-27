package Models;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;


@Data
@Entity
@Table(name = "Ue")
public class Ue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String libelle;
    @Column(nullable = false)
    private String code;

    private int nombreCredit;
    private int volHoraireTotal;
    private int volHoraireRealise;

    @Column(nullable = true)
    private String description;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "ue_etudiant",
            joinColumns = @JoinColumn(name="ue_id"),
            inverseJoinColumns = @JoinColumn(name="etudiant_id")
    )
    private Set<Etudiant> etudiantsEnregistre= new HashSet<>();

    @OneToMany(mappedBy = "ue")
    private Set<Cours> seanceDeCours= new HashSet<>();


}

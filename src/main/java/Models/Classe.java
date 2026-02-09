//package Models;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.util.Set;
//import java.util.HashSet;
//
//import java.util.HashSet;
//
//@Data
//@Entity
//@Table(name="classe")
//public class Classe {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Column(nullable = false)
//    private String nom;
//
//    private int effectif;
//
//    @ManyToOne
//    @JoinColumn(name = "filiere_id",nullable = false)
//    private Filiere filiere;
//
//
//    @OneToMany(mappedBy = "classe")
//    private Set<SeanceDeCours> seancesCours= new HashSet<>();
//
//    @OneToMany(mappedBy = "classe")
//    private Set<Etudiants> etudiants= new HashSet<>();
//
//}

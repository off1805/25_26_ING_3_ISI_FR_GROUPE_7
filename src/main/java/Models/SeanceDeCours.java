//package Models;
//
//import lombok.Data;
//
//import java.util.List;
//import jakarta.persistence.*;
//
//@Data
//@Entity
//public class SeanceDeCours {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    private Date dateDebut;
//    private Date dateFin;
//
//    @Enumerated(EnumType.STRING)
//    private StatutSeance statut;
//
//    // Relation avec EmploiDeTemps
//    @ManyToOne
//    @JoinColumn(name = "emploi_temps_id")
//    private EmploiDeTemps emploiDeTemps;
//
//    // Relation avec InfoPresence
//    @OneToMany(mappedBy = "seanceDeCours", cascade = CascadeType.ALL)
//    private List<InfoPresence> infoPresences;
//}

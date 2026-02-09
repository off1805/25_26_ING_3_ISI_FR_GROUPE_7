//package Models;
//
//import lombok.Data;
//
//import java.util.List;
//import jakarta.persistence.*;
//
//@Data
//@Entity
//public class InfoPresence {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//
//    private Boolean type;// true pour absence et false pour retard
//
//
//    private Boolean statut;//true pour justifié et false pour non justifé
//
//    @ElementCollection
//    private List<String> modeEnregistrement;
//
//    private Date dateDebut;
//    private Date dateFin;
//
//    // Relation avec SeanceDeCours
//    @ManyToOne
//    @JoinColumn(name = "seance_id")
//    private SeanceDeCours seanceDeCours;
//}

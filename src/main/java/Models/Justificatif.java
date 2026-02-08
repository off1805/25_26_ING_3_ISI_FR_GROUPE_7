//package Models;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "justificatif")
//public class Justificatif {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String description;
//
//    // nonTraite, encours, rejete
//    private String statutTraitement;
//
//    private LocalDateTime dateSoumission;
//    private LocalDateTime dateDerniereModif;
//    private LocalDateTime dateTraitement;
//
//    @ElementCollection
//    private List<String> piecesJointes;
//
//    private String motifRejet;
//
//
//    @ManyToOne
//    @JoinColumn(name = "info_presence_id")
//    private InfoPresence infoPresence;
//
//
//}
//
//

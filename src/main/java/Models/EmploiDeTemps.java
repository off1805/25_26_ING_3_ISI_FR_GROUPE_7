//package Models;
//import lombok.Data;
//
//import java.util.List;
//import jakarta.persistence.*;
//
//@Data
//@Entity
//public class EmploiDeTemps {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    private Date dateDebut;
//
//    private Date dateFin;
//
//    @OneToMany(mappedBy = "emploiDeTemps", cascade = CascadeType.ALL)
//    private List<SeanceDeCours> seances;
//}

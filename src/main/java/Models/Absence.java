package Models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "abscence")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;
    private Date dateFin;
    private int etudiantId;
    private int professeurId;
    private int ueId;
    private int justificatifId; // Ce champ sera null par défaut (voir note)
}

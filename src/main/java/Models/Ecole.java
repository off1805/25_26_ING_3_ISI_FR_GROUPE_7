package Models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ecole")
public class Ecole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false)
    protected String nom;

    protected long coorLatitude;
    protected long coorLongitude;
    protected long rayonLocalisation;

}

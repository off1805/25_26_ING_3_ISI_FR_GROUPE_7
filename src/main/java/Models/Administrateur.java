package Models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name="administrateur")


public class Administrateur extends Utilisateur{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

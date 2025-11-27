package Models;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.HashSet;

import java.util.HashSet;

@Entity
@Table(name = "filiere")
@Data
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nom;

    private String description;

    @OneToMany(mappedBy = "filiere")
    private  Set<Classe> classes= new HashSet<>();

    @OneToOne
    @JoinColumn(name="ap_id")
    private AssistantPedagogique ap;
}

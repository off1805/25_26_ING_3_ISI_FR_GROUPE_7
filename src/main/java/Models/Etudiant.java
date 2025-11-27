package Models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "etudiant")
@Data

public class Etudiant extends Utilisateur{
    protected String matricule;
    private int classeId;

    @ManyToOne
    public Parent parent;
    public Classe classe;
    public Ue ue;

    @OneToMany
    public Ue uE;
}

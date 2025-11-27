package Models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "parent")

public class Parent extends Utilisateur{

    @OneToMany
    public Etudiant etudiant;
}

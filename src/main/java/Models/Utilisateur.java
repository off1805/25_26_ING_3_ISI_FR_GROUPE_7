package Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "user")
abstract class  Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ide;
    //cle primaire de la table utilisateur
    protected int id;
    protected boolean etatCompte;
    protected String email;
    protected String motDePasse;
    protected String photoProfil;
    protected char sexe;
    protected String role;
    protected String nom;
    protected String prenom;
    protected Date dateNaissance;
    protected int numTel;
    protected Date dateCreation;

}

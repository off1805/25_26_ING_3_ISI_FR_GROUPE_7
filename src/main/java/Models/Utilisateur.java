package Models;


import java.util.Date;


abstract class Utilisateur {
    //cle primaire de la table utilisateur
    protected int id;
    protected boolean etatCompte;
    protected String email;

    protected String nom;

    protected String prenom;

    protected Date dateNaissance;
}

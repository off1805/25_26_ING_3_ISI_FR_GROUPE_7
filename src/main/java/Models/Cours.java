//package Models;
//
//import jakarta.persistence.*;
//
//import java.util.Date;
//
//
//@Entity
//@Table(name = "cours")
//public class Cours {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateDebut;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateFin;
//    private int classeId;
//    private int ueId;
//    private int professeurId;
//
//    public Date getDateDebut() {
//        return dateDebut;
//    }
//
//    public void setDateDebut(Date dateDebut) {
//        this.dateDebut = dateDebut;
//    }
//
//    public Date getDateFin() {
//        return dateFin;
//    }
//
//    public void setDateFin(Date dateFin) {
//        this.dateFin = dateFin;
//    }
//
//    public int getClasseId() {
//        return classeId;
//    }
//
//    public void setClasseId(int classeId) {
//        this.classeId = classeId;
//    }
//
//    public int getUeId() {
//        return ueId;
//    }
//
//    public void setUeId(int ueId) {
//        this.ueId = ueId;
//    }
//
//    public int getProfesseurId() {
//        return professeurId;
//    }
//
//    public void setProfesseurId(int professeurId) {
//        this.professeurId = professeurId;
//    }
//}

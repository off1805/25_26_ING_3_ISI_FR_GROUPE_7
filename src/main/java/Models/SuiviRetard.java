package Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "suiviRetard")
public class SuiviRetard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ElementCollection
    private List<Boolean> tabAbsence;

    private LocalDateTime dateMiseAJour;

    private LocalDate dateDebut;
    private LocalDate dateFin;
}

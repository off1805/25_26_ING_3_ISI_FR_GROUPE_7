package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @GetMapping("/dashboard")
    public String dashboardView(Model model) {
        UserDetailsResponseDTO teacher = getFakeTeacher();
        List<DashboardSeanceViewModel> seancesJour = getFakeDashboardSeancesJour();

        DashboardNextCourseViewModel nextCourse = seancesJour.stream()
                .filter(seance -> seance.status() == DashboardSeanceStatus.EN_COURS
                        || seance.status() == DashboardSeanceStatus.A_VENIR)
                .findFirst()
                .map(seance -> new DashboardNextCourseViewModel(
                        seance.heureDebut(),
                        seance.libelle()
                ))
                .orElse(null);

        model.addAttribute("teacher", teacher);
        model.addAttribute("seancesJour", seancesJour);
        model.addAttribute("nextCourse", nextCourse);

        return "TeacherInterface/TeacherDashboard";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
        UserDetailsResponseDTO teacher = getFakeTeacher();
        List<SeanceViewModel> seances = getFakeSchedule();

        model.addAttribute("teacher", teacher);
        model.addAttribute("seances", seances);

        return "TeacherInterface/TeacherSchedule";
    }

    @GetMapping("/seance")
    public String seanceView(Model model) {
        UserDetailsResponseDTO teacher = getFakeTeacher();
        SeanceViewModel seance = getFakeCurrentSeance();
        List<StudentViewModel> etudiants = getFakeStudents();

        model.addAttribute("teacher", teacher);
        model.addAttribute("seance", seance);
        model.addAttribute("etudiants", etudiants);

        return "TeacherInterface/TeacherSeance";
    }

    private UserDetailsResponseDTO getFakeTeacher() {
        return new UserDetailsResponseDTO(
                1L,
                UserStatus.ACTIVE,
                "enseignant@kemoschool.com",
                "TEACHER",
                101L,
                "Ngono",
                "Terence"
        );
    }

    private SeanceViewModel getFakeCurrentSeance() {
        return new SeanceViewModel(
                1L,
                "Architecture des ordinateurs",
                "Salle B12",
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                11L
        );
    }

    private List<SeanceViewModel> getFakeSchedule() {
        return List.of(
                new SeanceViewModel(
                        1L,
                        "Architecture des ordinateurs",
                        "Salle B12",
                        LocalDate.now(),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        11L
                ),
                new SeanceViewModel(
                        2L,
                        "Réseaux informatiques",
                        "Salle C04",
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        12L
                ),
                new SeanceViewModel(
                        3L,
                        "Bases de données",
                        "Salle A07",
                        LocalDate.now().plusDays(2),
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0),
                        13L
                ),
                new SeanceViewModel(
                        4L,
                        "Mathématiques appliquées",
                        "Salle D02",
                        LocalDate.now().plusDays(3),
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0),
                        14L
                ),
                new SeanceViewModel(
                        5L,
                        "Programmation Java",
                        "Salle E03",
                        LocalDate.now().plusDays(4),
                        LocalTime.of(13, 0),
                        LocalTime.of(15, 0),
                        15L
                ),
                new SeanceViewModel(
                        6L,
                        "Systèmes d'exploitation",
                        "Salle F01",
                        LocalDate.now().plusDays(5),
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0),
                        16L
                )
        );
    }

    private List<StudentViewModel> getFakeStudents() {
        return List.of(
                new StudentViewModel(1L, "Aminata", "Bah", "23L3I001"),
                new StudentViewModel(2L, "Kevin", "Foka", "23L3I002"),
                new StudentViewModel(3L, "Sarah", "Njoya", "23L3I003"),
                new StudentViewModel(4L, "Merveille", "Tchoumi", "23L3I004"),
                new StudentViewModel(5L, "Jordan", "Essomba", "23L3I005"),
                new StudentViewModel(6L, "Prisca", "Ngassa", "23L3I006"),
                new StudentViewModel(7L, "Blaise", "Mvondo", "23L3I007"),
                new StudentViewModel(8L, "Esther", "Kouam", "23L3I008")
        );
    }

    private List<DashboardSeanceViewModel> getFakeDashboardSeancesJour() {
        return List.of(
                new DashboardSeanceViewModel(
                        1L,
                        "Mathématiques",
                        "Salle A12",
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0),
                        DashboardSeanceStatus.EN_COURS
                ),
                new DashboardSeanceViewModel(
                        2L,
                        "Architecture des ordinateurs",
                        "Salle B08",
                        LocalTime.of(11, 0),
                        LocalTime.of(13, 0),
                        DashboardSeanceStatus.A_VENIR
                ),
                new DashboardSeanceViewModel(
                        3L,
                        "Bases de données",
                        "Salle C05",
                        LocalTime.of(15, 0),
                        LocalTime.of(17, 0),
                        DashboardSeanceStatus.TERMINE
                )
        );
    }

    public record SeanceViewModel(
            Long id,
            String libelle,
            String salle,
            LocalDate dateSeance,
            LocalTime heureDebut,
            LocalTime heureFin,
            Long coursId
    ) {
    }

    public record StudentViewModel(
            Long id,
            String prenom,
            String nom,
            String matricule
    ) {
    }

    public record DashboardSeanceViewModel(
            Long id,
            String libelle,
            String salle,
            LocalTime heureDebut,
            LocalTime heureFin,
            DashboardSeanceStatus status
    ) {
    }

    public record DashboardNextCourseViewModel(
            LocalTime heureDebut,
            String libelle
    ) {
    }

    public enum DashboardSeanceStatus {
        EN_COURS,
        A_VENIR,
        TERMINE
    }
}
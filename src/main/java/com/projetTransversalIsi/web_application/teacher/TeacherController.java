package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.ProfileResponseDTO;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final com.projetTransversalIsi.emploi_temps.application.service.SeanceService seanceService;
    private final com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository studentProfileRepo;
    private final com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataEmploiTempsRepository emploiTempsRepo;
    private final com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository userRepository;
    private final com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository presenceListRepo;

    @GetMapping("/dashboard")
    public String dashboardView(@org.springframework.security.core.annotation.AuthenticationPrincipal com.projetTransversalIsi.security.domain.UserPrincipal principal, Model model) {
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
    public String scheduleView(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            com.projetTransversalIsi.security.domain.UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                model.addAttribute("teacherId", user.getProfile().getId());
                String displayName = user.getProfile().getPrenom() + " " + user.getProfile().getNom();
                model.addAttribute("className", displayName.trim());
            }
        }

        return "TeacherInterface/TeacherSchedule";
    }

    @GetMapping("/seance")
    public String seanceView(@org.springframework.security.core.annotation.AuthenticationPrincipal com.projetTransversalIsi.security.domain.UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth/login";

        // 1. Récupérer le profile de l'enseignant
        var user = userRepository.findById(principal.userId()).orElse(null);
        if (user == null || user.getProfile() == null) {
            return "redirect:/auth/login";
        }
        Long teacherProfileId = user.getProfile().getId();

        // 2. Trouver la séance en cours (aujourd'hui)
        List<com.projetTransversalIsi.emploi_temps.domain.model.Seance> seancesAujourdhui = 
                seanceService.getSeancesTodayByEnseignant(teacherProfileId, false);
        
        java.time.LocalTime now = java.time.LocalTime.now();
        com.projetTransversalIsi.emploi_temps.domain.model.Seance currentSeance = seancesAujourdhui.stream()
                .filter(s -> !now.isBefore(s.getHeureDebut()) && !now.isAfter(s.getHeureFin()))
                .findFirst()
                .orElse(null);

        if (currentSeance != null) {
            float totalHours = java.time.Duration.between(currentSeance.getHeureDebut(), currentSeance.getHeureFin()).toMinutes() / 60.0f;
            float markedHours = presenceListRepo.findBySeanceId(currentSeance.getId()).stream()
                    .findFirst()
                    .map(com.projetTransversalIsi.emploi_temps.domain.model.PresenceList::getHeuresMarquer)
                    .orElse(0.0f);

            SeanceViewModel seanceVM = new SeanceViewModel(
                    currentSeance.getId(),
                    currentSeance.getLibelle(),
                    currentSeance.getSalle(),
                    currentSeance.getDateSeance(),
                    currentSeance.getHeureDebut(),
                    currentSeance.getHeureFin(),
                    currentSeance.getCoursId(),
                    totalHours,
                    markedHours
            );
            
            // 3. Trouver les étudiants via l'EmploiTemps
            List<StudentViewModel> students = emploiTempsRepo.findBySeanceId(currentSeance.getId())
                    .map(emploi -> studentProfileRepo.findByClasseId(emploi.getClasseId()))
                    .map(list -> list.stream()
                            .map(p -> new StudentViewModel(p.getId(), p.getPrenom(), p.getNom(), p.getMatricule(), p.getPhotoUrl()))
                            .collect(java.util.stream.Collectors.toList()))
                    .orElse(java.util.Collections.emptyList());

            model.addAttribute("seance", seanceVM);
            model.addAttribute("etudiants", students);
        } else {
            model.addAttribute("seance", null);
            model.addAttribute("etudiants", java.util.Collections.emptyList());
        }

        model.addAttribute("enseignantId", teacherProfileId);
        return "TeacherInterface/TeacherSeance";
    }

    private UserDetailsResponseDTO getFakeTeacher() {
        return new UserDetailsResponseDTO(
                1L,
                UserStatus.ACTIVE,
                "enseignant@kemoschool.com",
                "TEACHER",
                ProfileResponseDTO.builder()
                        .id(101L)
                        .nom("Ngono")
                        .prenom("Terence")
                        .matricule("FAKE-MAT-001")
                        .numeroTelephone("677777777")
                        .titre("Dr")
                        .specialite("Informatique")
                        .type("PERMANENT")
                        .build()
        );
    }


    private SeanceViewModel getFakeCurrentSeance() {
        return new SeanceViewModel(
                1L,
                "Architecture des ordinateurs",
                "Salle B12",
                LocalDate.now(),
                LocalTime.of(23, 0),
                LocalTime.of(23, 50),
                11L,
                4.0f,
                0.0f
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
                        11L,
                        4.0f,
                        0.0f
                ),
                new SeanceViewModel(
                        2L,
                        "Réseaux informatiques",
                        "Salle C04",
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        12L,
                        2.0f,
                        0.0f
                ),
                new SeanceViewModel(
                        3L,
                        "Bases de données",
                        "Salle A07",
                        LocalDate.now().plusDays(2),
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0),
                        13L,
                        2.0f,
                        0.0f
                ),
                new SeanceViewModel(
                        4L,
                        "Mathématiques appliquées",
                        "Salle D02",
                        LocalDate.now().plusDays(3),
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0),
                        14L,
                        2.0f,
                        0.0f
                ),
                new SeanceViewModel(
                        5L,
                        "Programmation Java",
                        "Salle E03",
                        LocalDate.now().plusDays(4),
                        LocalTime.of(13, 0),
                        LocalTime.of(15, 0),
                        15L,
                        2.0f,
                        0.0f
                ),
                new SeanceViewModel(
                        6L,
                        "Systèmes d'exploitation",
                        "Salle F01",
                        LocalDate.now().plusDays(5),
                        LocalTime.of(8, 0),
                        LocalTime.of(10, 0),
                        16L,
                        2.0f,
                        0.0f
                )
        );
    }

    private List<StudentViewModel> getFakeStudents() {
        return List.of(
                new StudentViewModel(1L, "Aminata", "Bah", "23L3I001", null),
                new StudentViewModel(2L, "Kevin", "Foka", "23L3I002", null),
                new StudentViewModel(3L, "Sarah", "Njoya", "23L3I003", null),
                new StudentViewModel(4L, "Merveille", "Tchoumi", "23L3I004", null),
                new StudentViewModel(5L, "Jordan", "Essomba", "23L3I005", null),
                new StudentViewModel(6L, "Prisca", "Ngassa", "23L3I006", null),
                new StudentViewModel(7L, "Blaise", "Mvondo", "23L3I007", null),
                new StudentViewModel(8L, "Esther", "Kouam", "23L3I008", null)
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
            Long coursId,
            float totalHours,
            float markedHours
    ) {
    }

    public record StudentViewModel(
            Long id,
            String prenom,
            String nom,
            String matricule,
            String photoUrl
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
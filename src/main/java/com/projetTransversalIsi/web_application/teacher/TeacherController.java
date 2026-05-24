package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataEmploiTempsRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataPresenceListRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataSeanceRepository;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.enums.UserStatus;
import com.projetTransversalIsi.user.dto.ProfileResponseDTO;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserRepository userRepository;
    private final SpringDataEmploiTempsRepository emploiTempsRepository;
    private final SpringDataPresenceListRepository presenceListRepository;
    private final SpringDataSeanceRepository seanceRepository;
    @GetMapping("/cours")
    public String coursView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        UserDetailsResponseDTO teacher = getFakeTeacher();
        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                teacher = new UserDetailsResponseDTO(
                        user.getId(), user.getStatus(), user.getEmail(), "TEACHER",
                        ProfileResponseDTO.builder()
                                .id(user.getProfile().getId())
                                .nom(user.getProfile().getNom())
                                .prenom(user.getProfile().getPrenom())
                                .build());
            }
        }
        model.addAttribute("teacher", teacher);
        return "TeacherInterface/TeacherCours";
    }

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
    public String seanceView(@AuthenticationPrincipal UserPrincipal principal, Model model) {

        // ── Enseignant authentifié ─────────────────────────────────────────
        UserDetailsResponseDTO teacher = getFakeTeacher();
        Long enseignantProfileId = null;

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                enseignantProfileId = user.getProfile().getId();
            }
        }
        // Fallback sur le profil fake si pas d'auth (dev)
        if (enseignantProfileId == null && teacher.profile() != null) {
            enseignantProfileId = teacher.profile().getId();
        }

        // ── Séance en cours (today, heure actuelle, cet enseignant) ───────
        final Long finalEnseignantId = enseignantProfileId;
        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();

        SeanceViewModel seance = (finalEnseignantId == null) ? null :
                seanceRepository.findByEnseignantId(finalEnseignantId).stream()
                        .filter(s -> !s.isDeleted()
                                && s.getDateSeance().equals(today)
                                && !s.getHeureDebut().isAfter(now)
                                && !s.getHeureFin().isBefore(now))
                        .findFirst()
                        .map(s -> new SeanceViewModel(
                                s.getId(), s.getLibelle(), s.getSalle(),
                                s.getDateSeance(), s.getHeureDebut(), s.getHeureFin(),
                                s.getCoursId()))
                        .orElse(null);

        // ── Résolution de la classeId via emploi du temps ou liste de présence ──
        Long classeId = null;
        Long presenceListId = null;
        boolean hasListePresence = false;

        if (seance != null) {
            var presenceLists = presenceListRepository.findBySeanceId(seance.id());
            hasListePresence = !presenceLists.isEmpty();

            if (!presenceLists.isEmpty()) {
                presenceListId = presenceLists.get(0).getId();
                classeId = presenceLists.get(0).getClasseId();
            } else {
                classeId = emploiTempsRepository
                        .findBySeanceId(seance.id())
                        .map(et -> et.getClasseId())
                        .orElse(null);
            }
        }

        // Les étudiants sont chargés côté client via /api/students/classes/{classeId}
        model.addAttribute("teacher", teacher);
        model.addAttribute("seance", seance);
        model.addAttribute("classeId", classeId);
        model.addAttribute("enseignantId", finalEnseignantId);
        model.addAttribute("hasListePresence", hasListePresence);
        model.addAttribute("presenceListId", presenceListId);
        model.addAttribute("coursId", seance != null ? seance.coursId() : null);

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
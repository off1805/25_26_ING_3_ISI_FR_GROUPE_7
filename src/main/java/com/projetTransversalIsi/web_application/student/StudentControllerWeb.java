package com.projetTransversalIsi.web_application.student;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataEmploiTempsRepository;
import com.projetTransversalIsi.security.domain.UserPrincipal;
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
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentControllerWeb {

    private final com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository userRepository;
    private final com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository studentProfileRepo;
    private final SpringDataPresenceRowRepository presenceRowRepository;
    private final SpringDataEmploiTempsRepository emploiTempsRepository;

    @GetMapping("/dashboard")
    public String dashboardView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        UserDetailsResponseDTO student = getFakeStudent();
        String classeCode   = null;
        String filiereNom   = null;
        int totalPresents   = 0;
        int totalAbsents    = 0;
        int tauxPresence    = 0;

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                var profile = user.getProfile();
                student = new UserDetailsResponseDTO(
                        user.getId(), user.getStatus(), user.getEmail(), "STUDENT",
                        ProfileResponseDTO.builder()
                                .id(profile.getId())
                                .nom(profile.getNom())
                                .prenom(profile.getPrenom())
                                .matricule(profile.getMatricule())
                                .build());

                // Classe info
                var sp = studentProfileRepo.findById(profile.getId()).orElse(null);
                if (sp != null && sp.getClasse() != null) {
                    classeCode = sp.getClasse().getCode();
                    var spec = sp.getClasse().getSpecialite();
                    if (spec != null && spec.getNiveau() != null && spec.getNiveau().getFiliere() != null) {
                        filiereNom = spec.getNiveau().getFiliere().getNom();
                    }
                }

                // Présence stats
                var rows = presenceRowRepository.findByEtudiantId(profile.getId());
                totalPresents = (int) rows.stream().filter(r -> Boolean.TRUE.equals(r.getPresent())).count();
                totalAbsents  = (int) rows.stream().filter(r -> Boolean.FALSE.equals(r.getPresent())).count();
                int total = totalPresents + totalAbsents;
                tauxPresence  = total > 0 ? (int) Math.round((totalPresents * 100.0) / total) : 0;
            }
        }

        // Séances du jour pour l'étudiant (via sa classe)
        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();
        List<SeanceJourViewModel> seancesJour = new java.util.ArrayList<>();

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                var sp = studentProfileRepo.findById(user.getProfile().getId()).orElse(null);
                if (sp != null && sp.getClasse() != null) {
                    Long classeId = sp.getClasse().getId();
                    emploiTempsRepository.findByClasseId(classeId).stream()
                            .filter(et -> !et.isDeleted())
                            .flatMap(et -> et.getSeances().stream())
                            .filter(s -> !s.isDeleted() && s.getDateSeance().equals(today))
                            .forEach(s -> {
                                String status;
                                if (s.getHeureFin().isBefore(now))       status = "TERMINE";
                                else if (s.getHeureDebut().isAfter(now)) status = "A_VENIR";
                                else                                      status = "EN_COURS";
                                seancesJour.add(new SeanceJourViewModel(s.getLibelle(), s.getSalle(), s.getHeureDebut(), s.getHeureFin(), status));
                            });
                    seancesJour.sort(java.util.Comparator.comparing(SeanceJourViewModel::heureDebut));
                }
            }
        }

        // Initiales étudiant (calculées côté Java pour éviter NPE Thymeleaf)
        String studentInitials = "ET";
        if (student != null && student.profile() != null) {
            String p = student.profile().getPrenom() != null ? student.profile().getPrenom() : "";
            String n = student.profile().getNom()    != null ? student.profile().getNom()    : "";
            studentInitials = (p.isEmpty() ? "?" : p.substring(0,1).toUpperCase())
                            + (n.isEmpty() ? "?" : n.substring(0,1).toUpperCase());
        }

        String nextSeanceLabel = null;
        if (!seancesJour.isEmpty()) {
            seancesJour.stream()
                .filter(s -> "EN_COURS".equals(s.status()) || "A_VENIR".equals(s.status()))
                .findFirst()
                .ifPresent(s -> { /* stored below */ });
            var ns = seancesJour.stream()
                .filter(s -> "EN_COURS".equals(s.status()) || "A_VENIR".equals(s.status()))
                .findFirst().orElse(null);
            if (ns != null) nextSeanceLabel = ns.heureDebut() + " — " + ns.libelle();
        }

        model.addAttribute("student",          student);
        model.addAttribute("studentInitials",  studentInitials);
        model.addAttribute("classeCode",       classeCode);
        model.addAttribute("filiereNom",       filiereNom);
        model.addAttribute("totalPresents",    totalPresents);
        model.addAttribute("totalAbsents",     totalAbsents);
        model.addAttribute("tauxPresence",     tauxPresence);
        model.addAttribute("seancesJour",      seancesJour);
        model.addAttribute("nextSeanceLabel",  nextSeanceLabel);
        return "StudentInterface/StudentDashboard";
    }

    public record SeanceJourViewModel(String libelle, String salle, LocalTime heureDebut, LocalTime heureFin, String status) {}

    @GetMapping("/schedule")
    public String scheduleView(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                studentProfileRepo.findById(user.getProfile().getId()).ifPresent(sp -> {
                    if (sp.getClasse() != null) {
                        model.addAttribute("classId",   sp.getClasse().getId());
                        model.addAttribute("className", sp.getClasse().getCode());
                    }
                });
            }
        }
        return "StudentInterface/StudentSchedule";
    }

    @GetMapping("/attendance")
    public String attendanceView(Model model) {
        model.addAttribute("student", getFakeStudent());
        return "StudentInterface/StudentAttendance";
    }

    @GetMapping("/subjects")
    public String subjectsView(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                studentProfileRepo.findById(user.getProfile().getId()).ifPresent(sp -> {
                    if (sp.getClasse() != null) {
                        model.addAttribute("classeId", sp.getClasse().getId());
                        if (sp.getClasse().getSpecialite() != null) {
                            model.addAttribute("specialiteId", sp.getClasse().getSpecialite().getId());
                        }
                    }
                    model.addAttribute("student", new UserDetailsResponseDTO(
                            user.getId(),
                            user.getStatus(),
                            user.getEmail(),
                            "STUDENT",
                            ProfileResponseDTO.builder()
                                    .id(user.getProfile().getId())
                                    .nom(user.getProfile().getNom())
                                    .prenom(user.getProfile().getPrenom())
                                    .build()
                    ));
                });
            }
        }

        if (!model.containsAttribute("student")) {
            model.addAttribute("student", getFakeStudent());
        }

        return "StudentInterface/StudentSubjects";
    }

    @GetMapping("/absences")
    public String absencesView(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model) {

        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                var profile = user.getProfile();
                model.addAttribute("student", new UserDetailsResponseDTO(
                        user.getId(),
                        user.getStatus(),
                        user.getEmail(),
                        "STUDENT",
                        ProfileResponseDTO.builder()
                                .id(profile.getId())
                                .nom(profile.getNom())
                                .prenom(profile.getPrenom())
                                .build()
                ));
            }
        } else {
            model.addAttribute("student", getFakeStudent());
        }
        return "StudentInterface/StudentAbsenceJustification";
    }

    private UserDetailsResponseDTO getFakeStudent() {
        return new UserDetailsResponseDTO(
                1L,
                UserStatus.ACTIVE,
                "etudiant@kemoschool.com",
                "STUDENT",
                ProfileResponseDTO.builder()
                        .id(201L)
                        .nom("Dupont")
                        .prenom("Jean")
                        .matricule("23L3I001")
                        .numeroTelephone("677777777")
                        .build()
        );
    }

    public record AbsenceViewModel(
            Long id,
            String subject,
            LocalDate date,
            String status,
            String reason
    ) {}
}

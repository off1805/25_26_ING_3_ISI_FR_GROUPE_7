package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.emploi_temps.application.dto.SearchSeanceRequestDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.service.SeanceService;
import com.projetTransversalIsi.emploi_temps.application.use_cases.GetCurrentSeanceUC;
import com.projetTransversalIsi.user.services.FindUserByIdUC;          // CORR #1 : bon package
import com.projetTransversalIsi.user.domain.User;                       // CORR #2 : execute() retourne User
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    private final FindUserByIdUC findUserByIdUC;
    private final GetCurrentSeanceUC getCurrentSeanceUC;
    private final SeanceService seanceService;
    private final ObjectMapper objectMapper;

    // ── Routes ───────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboardView(Model model) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // CORR #2 : execute() retourne User, on convertit ensuite en DTO
        User userDomain = findUserByIdUC.execute(userId);
        UserDetailsResponseDTO teacher = UserDetailsResponseDTO.fromDomain(userDomain);
        Long enseignantId = teacher.profile() != null ? teacher.profile().getId() : null;

        List<DashboardSeanceViewModel> seancesJour = List.of();
        if (enseignantId != null) {
            // CORR #3 : getSeancesTodayByEnseignant retourne List<Seance> (domaine)
            //           → on convertit via SeanceResponseDTO.fromDomain() puis on
            //             utilise les accesseurs record (heureDebut(), etc.)
            seancesJour = seanceService.getSeancesTodayByEnseignant(enseignantId, false)
                    .stream()
                    .map(seance -> {
                        SeanceResponseDTO dto = SeanceResponseDTO.fromDomain(seance);
                        DashboardSeanceStatus status = computeStatus(dto.heureDebut(), dto.heureFin());
                        return new DashboardSeanceViewModel(
                                dto.id(),
                                dto.libelle(),
                                dto.salle(),
                                dto.heureDebut(),
                                dto.heureFin(),
                                status
                        );
                    })
                    .toList();
        }

        DashboardNextCourseViewModel nextCourse = seancesJour.stream()
                .filter(s -> s.status() == DashboardSeanceStatus.EN_COURS
                        || s.status() == DashboardSeanceStatus.A_VENIR)
                .findFirst()
                .map(s -> new DashboardNextCourseViewModel(s.heureDebut(), s.libelle()))
                .orElse(null);

        model.addAttribute("teacher", teacher);
        model.addAttribute("seancesJour", seancesJour);
        model.addAttribute("nextCourse", nextCourse);

        return "TeacherInterface/TeacherDashboard";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) throws JsonProcessingException {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // CORR #2 : idem
        User userDomain = findUserByIdUC.execute(userId);
        UserDetailsResponseDTO teacher = UserDetailsResponseDTO.fromDomain(userDomain);
        Long enseignantId = teacher.profile() != null ? teacher.profile().getId() : null;

        List<SeanceResponseDTO> seances = List.of();
        if (enseignantId != null) {
            // CORR #4 : getSeancesByEnseignant() n'existe pas dans SeanceService
            //           → on utilise searchSeances() avec le critère enseignantId
            seances = seanceService.searchSeances(
                    new SearchSeanceRequestDTO(null, enseignantId, null, false)
            );
        }

        // Sérialise en format attendu par le JS de TeacherSchedule.html
        List<Map<String, Object>> seancesJson = seances.stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",       s.id());
                    // MONDAY=1 → 0, ..., SATURDAY=6 → 5
                    m.put("day",      s.dateSeance().getDayOfWeek().getValue() - 1);
                    m.put("start",    s.heureDebut().getHour());
                    m.put("end",      s.heureFin().getHour());
                    m.put("subject",  s.libelle());
                    m.put("class",    "");
                    m.put("room",     s.salle());
                    m.put("students", 0);
                    m.put("color",    s.couleur() != null ? s.couleur() : "#7c3aed");
                    return m;
                })
                .toList();

        model.addAttribute("teacher", teacher);
        model.addAttribute("seances", seances);
        model.addAttribute("seancesJson", objectMapper.writeValueAsString(seancesJson));

        return "TeacherInterface/TeacherSchedule";
    }

    @GetMapping("/seance")
    public String seanceView(Model model) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // CORR #2 : idem
        User userDomain = findUserByIdUC.execute(userId);
        UserDetailsResponseDTO teacher = UserDetailsResponseDTO.fromDomain(userDomain);
        Long enseignantId = teacher.profile() != null ? teacher.profile().getId() : null;

        SeanceResponseDTO seance = null;
        if (enseignantId != null) {
            seance = getCurrentSeanceUC.execute(enseignantId).orElse(null);
        }

        model.addAttribute("teacher", teacher);
        model.addAttribute("seance", seance);
        model.addAttribute("etudiants", List.of());
        model.addAttribute("enseignantId", enseignantId);

        return "TeacherInterface/TeacherSeance";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private DashboardSeanceStatus computeStatus(LocalTime debut, LocalTime fin) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(debut)) return DashboardSeanceStatus.A_VENIR;
        if (now.isAfter(fin))    return DashboardSeanceStatus.TERMINE;
        return DashboardSeanceStatus.EN_COURS;
    }

    // ── Records internes (utilisés par les templates Thymeleaf) ──────────────

    public record SeanceViewModel(
            Long id,
            String libelle,
            String salle,
            java.time.LocalDate dateSeance,
            LocalTime heureDebut,
            LocalTime heureFin,
            Long coursId
    ) {}

    public record StudentViewModel(
            Long id,
            String prenom,
            String nom,
            String matricule
    ) {}

    public record DashboardSeanceViewModel(
            Long id,
            String libelle,
            String salle,
            LocalTime heureDebut,
            LocalTime heureFin,
            DashboardSeanceStatus status
    ) {}

    public record DashboardNextCourseViewModel(
            LocalTime heureDebut,
            String libelle
    ) {}

    public enum DashboardSeanceStatus {
        EN_COURS,
        A_VENIR,
        TERMINE
    }
}

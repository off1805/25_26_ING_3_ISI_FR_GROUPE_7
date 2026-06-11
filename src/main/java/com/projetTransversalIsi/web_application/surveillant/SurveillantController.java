package com.projetTransversalIsi.web_application.surveillant;

import com.projetTransversalIsi.emploi_temps.application.dto.RetardListResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.use_cases.GetOrCreateRetardListUC;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoRetardRowEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceListEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaRetardRowEntity;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataEmploiTempsRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataInfoRetardRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataPresenceListRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataRetardRowRepository;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataFiliereRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/surveillant")
@RequiredArgsConstructor
public class SurveillantController {

    private final UserRepository                     userRepository;
    private final SpringDataClasseRepository         classeRepository;
    private final SpringDataEmploiTempsRepository    emploiTempsRepository;
    private final SpringDataPresenceListRepository   presenceListRepository;
    private final SpringDataPresenceRowRepository    presenceRowRepository;
    private final SpringDataFiliereRepository        filiereRepository;
    private final SpringDataNiveauRepository         niveauRepository;
    private final SpringDataStudentProfileRepository studentProfileRepository;
    private final SpringDataRetardRowRepository      retardRowRepository;
    private final SpringDataInfoRetardRowRepository  infoRetardRowRepository;
    private final GetOrCreateRetardListUC            getOrCreateRetardListUC;

    // ─────────────────────────────────────────────────────────────
    //  Dashboard
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        LocalDate today   = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        // Seances du jour
        List<JpaEmploiTempsEntity> todayEmplois = emploiTempsRepository.findByPeriode(today);
        List<SeanceDuJourViewModel> seancesToday = new ArrayList<>();

        for (JpaEmploiTempsEntity et : todayEmplois) {
            if (et.isDeleted()) continue;
            Long classeId = et.getClasseId();
            String classeCode = classeRepository.findById(classeId)
                    .map(JpaClasseEntity::getCode).orElse("?");
            for (var s : et.getSeances()) {
                if (s.isDeleted() || !s.getDateSeance().equals(today)) continue;
                String status;
                if (s.getHeureFin().isBefore(nowTime))        status = "TERMINE";
                else if (s.getHeureDebut().isAfter(nowTime))  status = "A_VENIR";
                else                                           status = "EN_COURS";
                boolean hasAppel = !presenceListRepository.findBySeanceId(s.getId()).isEmpty();
                seancesToday.add(new SeanceDuJourViewModel(
                        s.getId(), classeCode, classeId,
                        s.getLibelle(), s.getSalle(),
                        s.getHeureDebut(), s.getHeureFin(),
                        status, hasAppel));
            }
        }
        seancesToday.sort(Comparator.comparing(SeanceDuJourViewModel::heureDebut));

        // Stats presence du jour
        List<Long> todayPresenceListIds = presenceListRepository.findByDeletedFalse()
                .stream()
                .filter(p -> today.equals(p.getDate()))
                .map(JpaPresenceListEntity::getId)
                .collect(Collectors.toList());

        long totalPresents = 0, totalAbsents = 0;
        if (!todayPresenceListIds.isEmpty()) {
            var todayRows = todayPresenceListIds.stream()
                    .flatMap(id -> presenceRowRepository.findByPresenceListId(id).stream())
                    .collect(Collectors.toList());
            totalPresents = todayRows.stream().filter(r -> Boolean.TRUE.equals(r.getPresent())).count();
            totalAbsents  = todayRows.stream().filter(r -> Boolean.FALSE.equals(r.getPresent())).count();
        }

        long totalMarques      = totalPresents + totalAbsents;
        long seancesEnCours    = seancesToday.stream().filter(s -> "EN_COURS".equals(s.status())).count();
        long seancesAujourdhui = seancesToday.size();
        long appelsDuJour      = seancesToday.stream().filter(SeanceDuJourViewModel::hasAppel).count();
        int tauxPresence       = totalMarques > 0
                ? (int) Math.round((totalPresents * 100.0) / totalMarques)
                : 0;

        model.addAttribute("seancesEnCours",    seancesEnCours);
        model.addAttribute("seancesAujourdhui", seancesAujourdhui);
        model.addAttribute("appelsDuJour",      appelsDuJour);
        model.addAttribute("totalPresents",     totalPresents);
        model.addAttribute("totalAbsents",      totalAbsents);
        model.addAttribute("totalMarques",      totalMarques);
        model.addAttribute("tauxPresence",      tauxPresence);
        model.addAttribute("seancesToday",      seancesToday);

        // Initiales Surveillant
        String survInitials = "SU";
        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                String p = user.getProfile().getPrenom() != null ? user.getProfile().getPrenom() : "";
                String n = user.getProfile().getNom()    != null ? user.getProfile().getNom()    : "";
                survInitials = (p.isEmpty() ? "S" : p.substring(0,1).toUpperCase())
                             + (n.isEmpty() ? "U" : n.substring(0,1).toUpperCase());
                model.addAttribute("sidebarFullName", (p + " " + n).trim());
            }
        }
        model.addAttribute("survInitials", survInitials);

        return "SurveillantInterface/SurveillantDashboard";
    }

    // ─────────────────────────────────────────────────────────────
    //  Classes
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/classes")
    public String classesList(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        LocalDate today   = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        Map<Long, SeanceInfo> activeByClass = new LinkedHashMap<>();
        for (JpaEmploiTempsEntity et : emploiTempsRepository.findByPeriode(today)) {
            if (et.isDeleted()) continue;
            et.getSeances().stream()
                    .filter(s -> !s.isDeleted()
                            && s.getDateSeance().equals(today)
                            && !s.getHeureDebut().isAfter(nowTime)
                            && !s.getHeureFin().isBefore(nowTime))
                    .findFirst()
                    .ifPresent(s -> activeByClass.putIfAbsent(et.getClasseId(),
                            new SeanceInfo(s.getLibelle(), s.getSalle(),
                                    s.getHeureDebut(), s.getHeureFin())));
        }

        List<ClasseWithFilterViewModel> classes = classeRepository.findAll().stream()
                .map(c -> {
                    var spec = c.getSpecialite();
                    var niv  = spec.getNiveau();
                    var fil  = niv.getFiliere();
                    return new ClasseWithFilterViewModel(
                            c.getId(), c.getCode(), c.getDescription(),
                            fil.getId(), fil.getCode(), fil.getNom(),
                            niv.getId(), niv.getOrdre(),
                            activeByClass.get(c.getId()));
                })
                .sorted(Comparator.comparing(ClasseWithFilterViewModel::code))
                .collect(Collectors.toList());

        model.addAttribute("classes",  classes);
        model.addAttribute("filieres", filiereRepository.findByDeletedFalse());
        model.addAttribute("niveaux",  niveauRepository.findByDeletedFalse());

        return "SurveillantInterface/SurveillantClasses";
    }

    // ─────────────────────────────────────────────────────────────
    //  Appel
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/classes/{classeId}/appel")
    public String appelView(@PathVariable Long classeId,
                            @AuthenticationPrincipal UserPrincipal principal,
                            Model model) {

        Long surveillantProfileId = null;
        if (principal != null) {
            var user = userRepository.findById(principal.userId()).orElse(null);
            if (user != null && user.getProfile() != null) {
                surveillantProfileId = user.getProfile().getId();
            }
        }

        LocalDate today   = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        SeanceViewModel seance   = null;
        Long presenceListId      = null;
        boolean hasListePresence = false;

        List<JpaEmploiTempsEntity> emplois = emploiTempsRepository.findByClasseId(classeId);
        outer:
        for (JpaEmploiTempsEntity et : emplois) {
            if (et.isDeleted()) continue;
            for (var s : et.getSeances()) {
                if (!s.isDeleted()
                        && s.getDateSeance().equals(today)
                        && !s.getHeureDebut().isAfter(nowTime)
                        && !s.getHeureFin().isBefore(nowTime)) {
                    seance = new SeanceViewModel(
                            s.getId(), s.getLibelle(), s.getSalle(),
                            s.getDateSeance(), s.getHeureDebut(), s.getHeureFin(),
                            s.getCoursId());
                    break outer;
                }
            }
        }

        if (seance != null) {
            var lists = presenceListRepository.findBySeanceId(seance.id());
            hasListePresence = !lists.isEmpty();
            if (!lists.isEmpty()) presenceListId = lists.get(0).getId();
        }

        var classe = classeRepository.findById(classeId).orElse(null);
        String classeCode = classe != null ? classe.getCode() : "Classe " + classeId;

        model.addAttribute("seance",           seance);
        model.addAttribute("classeId",         classeId);
        model.addAttribute("classeCode",       classeCode);
        model.addAttribute("surveillantId",    surveillantProfileId);
        model.addAttribute("hasListePresence", hasListePresence);
        model.addAttribute("presenceListId",   presenceListId);
        model.addAttribute("coursId",          seance != null ? seance.coursId() : null);

        return "SurveillantInterface/SurveillantAppel";
    }

    // ─────────────────────────────────────────────────────────────
    //  Retards
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/retards")
    public String retardsView(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        return retards(null, principal, model);
    }

    @GetMapping("/retards/{classeId}")
    public String retardsClasseView(@PathVariable Long classeId,
                                    @AuthenticationPrincipal UserPrincipal principal,
                                    Model model) {
        return retards(classeId, principal, model);
    }

    // Chargement JSON du tableau hebdomadaire d'une classe (sans rendu Thymeleaf, sans rechargement de page).
    @GetMapping("/retards/{classeId}/data")
    @ResponseBody
    public RetardSemaineViewModel retardsData(@PathVariable Long classeId) {
        return buildSemaineViewModel(classeId);
    }

    private String retards(Long classeId, UserPrincipal principal, Model model) {
        List<RetardClasseViewModel> classes = classeRepository.findAll().stream()
                .map(c -> new RetardClasseViewModel(c.getId(), c.getCode(), c.getDescription()))
                .sorted(Comparator.comparing(RetardClasseViewModel::code))
                .collect(Collectors.toList());

        model.addAttribute("classes",  classes);
        model.addAttribute("classeId", classeId);
        model.addAttribute("semaine",  classeId != null ? buildSemaineViewModel(classeId) : null);

        return "SurveillantInterface/SurveillantRetards";
    }

    private RetardSemaineViewModel buildSemaineViewModel(Long classeId) {
        LocalDate semaineDebut = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        RetardListResponseDTO retardList = getOrCreateRetardListUC.execute(classeId, semaineDebut);

        List<JpaRetardRowEntity> rows = retardRowRepository.findByRetardListId(retardList.id());
        List<Long> rowIds = rows.stream().map(JpaRetardRowEntity::getId).collect(Collectors.toList());
        Map<Long, List<JpaInfoRetardRowEntity>> infoByRow = infoRetardRowRepository.findByRetardRowIdIn(rowIds)
                .stream()
                .collect(Collectors.groupingBy(JpaInfoRetardRowEntity::getRetardRowId));

        List<String> jours = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            jours.add(capitalize(semaineDebut.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH)));
        }

        List<RetardEtudiantViewModel> etudiants = new ArrayList<>();
        for (JpaRetardRowEntity row : rows) {
            JpaStudentProfileEntity profile = studentProfileRepository.findById(row.getEtudiantId()).orElse(null);
            String nom = profile != null ? profile.getNom() : "?";
            String prenom = profile != null ? profile.getPrenom() : "";

            List<RetardCelluleViewModel> cellules = infoByRow.getOrDefault(row.getId(), List.of()).stream()
                    .sorted(Comparator.comparingInt(JpaInfoRetardRowEntity::getJourSemaine))
                    .map(info -> new RetardCelluleViewModel(info.getId(), info.getJourSemaine(), info.getDate(), info.isEnRetard()))
                    .collect(Collectors.toList());

            etudiants.add(new RetardEtudiantViewModel(row.getEtudiantId(), nom, prenom, cellules));
        }
        etudiants.sort(Comparator.comparing(RetardEtudiantViewModel::nom).thenComparing(RetardEtudiantViewModel::prenom));

        var classe = classeRepository.findById(classeId).orElse(null);
        String classeCode = classe != null ? classe.getCode() : "Classe " + classeId;

        return new RetardSemaineViewModel(
                classeCode, semaineDebut, semaineDebut.plusDays(5), jours, etudiants);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ─── Records ─────────────────────────────────────────────────

    public record SeanceDuJourViewModel(
            Long id, String classeCode, Long classeId,
            String libelle, String salle,
            LocalTime heureDebut, LocalTime heureFin,
            String status, boolean hasAppel) {}

    public record ClasseWithFilterViewModel(
            Long id, String code, String description,
            Long filiereId, String filiereCode, String filiereNom,
            Long niveauId, int niveauOrdre,
            SeanceInfo activeSeance) {}

    public record SeanceInfo(
            String libelle, String salle,
            LocalTime heureDebut, LocalTime heureFin) {}

    public record SeanceViewModel(
            Long id, String libelle, String salle,
            LocalDate dateSeance, LocalTime heureDebut, LocalTime heureFin,
            Long coursId) {}

    // ─── Retards : view models ───────────────────────────────────

    public record RetardClasseViewModel(Long id, String code, String description) {}

    public record RetardCelluleViewModel(Long id, int jourSemaine, LocalDate date, boolean enRetard) {}

    public record RetardEtudiantViewModel(
            Long etudiantId, String nom, String prenom,
            List<RetardCelluleViewModel> cellules) {}

    public record RetardSemaineViewModel(
            String classeCode, LocalDate semaineDebut, LocalDate semaineFin,
            List<String> jours, List<RetardEtudiantViewModel> etudiants) {}
}

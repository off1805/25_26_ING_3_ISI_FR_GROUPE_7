package com.projetTransversalIsi.web_application.admin;

import com.projetTransversalIsi.cycle.application.use_cases.FindCycleByIdUC;
import com.projetTransversalIsi.cycle.application.use_cases.GetAllCyclesUC;
import com.projetTransversalIsi.cycle.domain.Cycle;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.dto.UserFiltreDto;
import com.projetTransversalIsi.user.infrastructure.UserMapper;
import com.projetTransversalIsi.user.services.GetAllUserStaffUC;
import com.projetTransversalIsi.user.services.SearchUserUC;
import com.projetTransversalIsi.Filiere.application.dto.FiliereResponseDTO;
import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.application.services.DefaultFiliereService;
import com.projetTransversalIsi.Niveau.application.dto.NiveauResponseDTO;
import com.projetTransversalIsi.Niveau.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.application.services.DefaultNiveauService;
import com.projetTransversalIsi.specialite.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.specialite.application.services.DefaultSpecialiteService;
import com.projetTransversalIsi.classe.application.services.DefaultClasseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor

public class AdminController {
    private final GetAllUserStaffUC getAllUserStaffUC;
    private final SearchUserUC searchUserUC;
    private final GetAllCyclesUC getAllCyclesUC;
    private final FindCycleByIdUC findCycleByIdUC;
    private final DefaultFiliereService filiereService;
    private final DefaultNiveauService niveauService;
    private final DefaultSpecialiteService specialiteService;
    private final DefaultClasseService classeService;
    private final UserMapper userMapper;


    private static String bcSanitizeLabel(String label) {
        if (label == null) return "";
        // We use '|' and ':' as separators client-side; strip them from labels.
        return label.replace('|', ' ').replace(':', ' ').trim();
    }

    private static String bcItem(String label, String href) {
        String safeLabel = bcSanitizeLabel(label);
        if (safeLabel.isBlank()) return "";
        if (href == null || href.isBlank()) return safeLabel;
        return safeLabel + ":" + href;
    }

    private static String bcJoin(String... items) {
        StringJoiner joiner = new StringJoiner("|");
        for (String item : items) {
            if (item != null && !item.isBlank()) joiner.add(item);
        }
        return joiner.toString();
    }

    @GetMapping("/school")
    public String schoolView(Model model){
        Map <String,Object> users= new HashMap<>();
        model.addAttribute("cycles", getAllCyclesUC.execute());
        model.addAttribute("filieres", filiereService.searchFilieres(new SearchFiliereRequestDTO(null, null, null,false)));
        model.addAttribute("niveaux", niveauService.searchNiveaux(new SearchNiveauRequestDTO(null, null, false)));
        model.addAttribute("specialites", specialiteService.getAllSpecialites());
        return "AdminInterface/AdminCycles";
    }
    @GetMapping("/users")
    public String usersView(Model model){
        Page<User> users=searchUserUC.execute(new UserFiltreDto(null, List.of("AP","TEACHER","SURVEILLANT"),null,null), PageRequest.of(0, 10, Sort.by("id").descending()));
        model.addAttribute("staff", users.stream().map(UserDetailsResponseDTO::fromDomain).toList());
        return "AdminInterface/AdminUser";
    }

    @GetMapping("/classes")
    public String classesView(Model model){
        model.addAttribute("staff", getAllUserStaffUC.execute());
        return "AdminInterface/AdminClasses";
    }

    @GetMapping("/cycles/{id}")
    public String cycleDetailView(@PathVariable("id") Long id, Model model) {
        Cycle cycle = findCycleByIdUC.execute(id);
        model.addAttribute("cycle", cycle);
        model.addAttribute("filieres", filiereService.getFilieresByCycleId(id));
        model.addAttribute(
                "adminPageBreadcrumb",
                bcJoin(
                        bcItem("Cycles", "/admin/school"),
                        bcItem(cycle != null ? cycle.getName() : "Cycle", null)
                )
        );
        return "AdminInterface/cycle";
    }

    @GetMapping("/filieres/{id}")
    public String filiereDetailView(@PathVariable("id") Long id, Model model) {
        FiliereResponseDTO filiere = filiereService.getFiliereById(id);
        model.addAttribute("filiere", filiere);
        model.addAttribute("niveaux", niveauService.getNiveauxByFiliereId(id));
        model.addAttribute(
                "adminPageBreadcrumb",
                bcJoin(
                        bcItem("Cycles", "/admin/school"),
                        bcItem(filiere != null ? filiere.cycleName() : "Cycle",
                                filiere != null && filiere.cycleId() != null ? ("/admin/cycles/" + filiere.cycleId()) : null),
                        bcItem(filiere != null ? filiere.nom() : "Filiere", null)
                )
        );
        return "AdminInterface/filiere";
    }

    @GetMapping("/niveaux/{id}")
    public String niveauDetailView(@PathVariable("id") Long id, Model model) {
        NiveauResponseDTO niveau = niveauService.getNiveauById(id);
        model.addAttribute("niveau", niveau);
        model.addAttribute("specialites", specialiteService.getSpecialitesByNiveauId(id));
        FiliereResponseDTO filiere = (niveau != null && niveau.filiereId() != null)
                ? filiereService.getFiliereById(niveau.filiereId())
                : null;
        model.addAttribute(
                "adminPageBreadcrumb",
                bcJoin(
                        bcItem("Cycles", "/admin/school"),
                        bcItem(filiere != null ? filiere.cycleName() : "Cycle",
                                filiere != null && filiere.cycleId() != null ? ("/admin/cycles/" + filiere.cycleId()) : null),
                        bcItem(niveau != null ? niveau.filiereNom() : "Filiere",
                                niveau != null && niveau.filiereId() != null ? ("/admin/filieres/" + niveau.filiereId()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau", null)
                )
        );
        return "AdminInterface/niveau";
    }

    @GetMapping("/specialites/{id}")
    public String specialiteDetailView(@PathVariable("id") Long id, Model model) {
        SpecialiteResponseDTO specialite = specialiteService.getSpecialiteById(id);
        model.addAttribute("specialite", specialite);
        model.addAttribute("classes", classeService.getClassesBySpecialiteId(id));
        NiveauResponseDTO niveau = (specialite != null && specialite.niveauId() != null)
                ? niveauService.getNiveauById(specialite.niveauId())
                : null;
        FiliereResponseDTO filiere = (niveau != null && niveau.filiereId() != null)
                ? filiereService.getFiliereById(niveau.filiereId())
                : null;
        model.addAttribute(
                "adminPageBreadcrumb",
                bcJoin(
                        bcItem("Cycles", "/admin/school"),
                        bcItem(filiere != null ? filiere.cycleName() : "Cycle",
                                filiere != null && filiere.cycleId() != null ? ("/admin/cycles/" + filiere.cycleId()) : null),
                        bcItem(niveau != null ? niveau.filiereNom() : "Filiere",
                                niveau != null && niveau.filiereId() != null ? ("/admin/filieres/" + niveau.filiereId()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau",
                                niveau != null && niveau.id() != null ? ("/admin/niveaux/" + niveau.id()) : null),
                        bcItem(specialite != null ? specialite.libelle() : "Specialite", null)
                )
        );
        return "AdminInterface/specialite";
    }
}



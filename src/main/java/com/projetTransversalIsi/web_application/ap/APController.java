package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.Filiere.application.dto.FiliereResponseDTO;
import com.projetTransversalIsi.Filiere.application.services.DefaultFiliereService;
import com.projetTransversalIsi.Niveau.application.dto.NiveauResponseDTO;
import com.projetTransversalIsi.Niveau.application.services.DefaultNiveauService;
import com.projetTransversalIsi.classe.application.services.DefaultClasseService;
import com.projetTransversalIsi.specialite.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.specialite.application.services.DefaultSpecialiteService;
import com.projetTransversalIsi.ue.application.dto.UeFiltreDto;
import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.application.use_cases.SearchUeUC;
import com.projetTransversalIsi.ue.infrastructure.UeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.StringJoiner;

@Controller
@RequestMapping("/ap")
@RequiredArgsConstructor
public class APController {

    private final DefaultFiliereService filiereService;
    private final DefaultNiveauService niveauService;
    private final DefaultSpecialiteService specialiteService;
    private final DefaultClasseService classeService;
    private final SearchUeUC searchUeUC;
    private final UeMapper ueMapper;

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

    @GetMapping("/filiere/{id}")
    public String subjectsView(@PathVariable("id") Long id, Model model) {
        FiliereResponseDTO filiere = filiereService.getFiliereById(id);
        model.addAttribute("filiere", filiere);
        model.addAttribute("niveaux", niveauService.getNiveauxByFiliereId(id));
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : "Filiere", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name"); // à remplacer par l'utilisateur connecté
        return "APInterface/APSubjects";
    }

    @GetMapping("/niveau/{id}")
    public String niveauView(@PathVariable("id") Long id, Model model) {
        NiveauResponseDTO niveau = niveauService.getNiveauById(id);
        model.addAttribute("niveau", niveau);
        model.addAttribute("specialites", specialiteService.getSpecialitesByNiveauId(id));
        FiliereResponseDTO filiere = null;
        if (niveau != null && niveau.filiereId() != null) {
            filiere = filiereService.getFiliereById(niveau.filiereId());
            model.addAttribute("filiere", filiere);
        }
        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : (niveau != null ? niveau.filiereNom() : "Filiere"),
                                niveau != null && niveau.filiereId() != null ? ("/ap/filiere/" + niveau.filiereId()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APNiveau";
    }

    @GetMapping("/specialite/{id}")
    public String specialiteView(@PathVariable("id") Long id, Model model) {
        SpecialiteResponseDTO specialite = specialiteService.getSpecialiteById(id);
        model.addAttribute("specialite", specialite);
        model.addAttribute("classes", classeService.getClassesBySpecialiteId(id));

        // UE catalog (module UE). For now this lists UEs globally (not yet scoped by specialite).
        Page<Ue> uesPage = searchUeUC.execute(
                new UeFiltreDto(null, null, false),
                PageRequest.of(0, 20, Sort.by("id").descending())
        );
        model.addAttribute("ues", uesPage.map(ueMapper::toResponseDTO).getContent());

        NiveauResponseDTO niveau = null;
        FiliereResponseDTO filiere = null;
        if (specialite != null && specialite.niveauId() != null) {
            niveau = niveauService.getNiveauById(specialite.niveauId());
            model.addAttribute("niveau", niveau);
            if (niveau != null && niveau.filiereId() != null) {
                filiere = filiereService.getFiliereById(niveau.filiereId());
                model.addAttribute("filiere", filiere);
            }
        }

        model.addAttribute(
                "apPageBreadcrumb",
                bcJoin(
                        bcItem(filiere != null ? filiere.nom() : "Filiere",
                                filiere != null && filiere.id() != null ? ("/ap/filiere/" + filiere.id()) : null),
                        bcItem(niveau != null ? ("Niveau " + niveau.ordre()) : "Niveau",
                                niveau != null && niveau.id() != null ? ("/ap/niveau/" + niveau.id()) : null),
                        bcItem(specialite != null ? specialite.libelle() : "Specialite", null)
                )
        );
        model.addAttribute("activePage", "subjects");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APSpecialite";
    }

    @GetMapping("/schedule")
    public String scheduleView(Model model) {
        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/APSchedule";
    }

    @GetMapping("/schedule/edit")
    public String editScheduleView(Model model) {
        model.addAttribute("activePage", "schedule");
        model.addAttribute("apName", "AP Name");
        return "APInterface/EditSchedule";
    }
}

package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.InfoRetardRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.use_cases.ToggleInfoRetardRowUC;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retards")
@RequiredArgsConstructor
public class RetardController {

    private final ToggleInfoRetardRowUC toggleInfoRetardRowUC;

    // PUT /api/retards/info-rows/{id}/toggle — bascule l'état "en retard" d'une cellule
    // (sauvegarde immédiate au clic, pas de bouton "Enregistrer").
    @PutMapping("/info-rows/{id}/toggle")
    public InfoRetardRowResponseDTO toggle(@PathVariable Long id) {
        return toggleInfoRetardRowUC.execute(id);
    }
}

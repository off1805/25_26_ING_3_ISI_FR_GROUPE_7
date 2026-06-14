package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.AppelResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAppelDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.use_cases.CloseAllPendingAppelsUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.CloseAppelUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.CreateAppelUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.DeleteAppelUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.GetAppelUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.MarkStudentPresentCommand;
import com.projetTransversalIsi.emploi_temps.application.use_cases.MarkStudentPresentUC;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appels")
@RequiredArgsConstructor
public class AppelController {

    private final CreateAppelUC createAppelUC;
    private final GetAppelUC getAppelUC;
    private final DeleteAppelUC deleteAppelUC;
    private final MarkStudentPresentUC markStudentPresentUC;
    private final CloseAppelUC closeAppelUC;
    private final CloseAllPendingAppelsUC closeAllPendingAppelsUC;

    // POST /api/appels — l'enseignant démarre un appel (MANUEL, PIN ou QR) pour une liste de présence.
    // Pour QR/PIN, un AttendanceCode est créé en interne ; la réponse contient scanUrl (QR) ou valeur (PIN).
    @PostMapping
    public ResponseEntity<AppelResponseDTO> create(@RequestBody CreateAppelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createAppelUC.execute(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppelResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getAppelUC.getById(id));
    }

    // GET /api/appels/presence-list/{id} — tous les appels d'une liste de présence.
    @GetMapping("/presence-list/{presenceListId}")
    public ResponseEntity<List<AppelResponseDTO>> getByPresenceList(@PathVariable Long presenceListId) {
        return ResponseEntity.ok(getAppelUC.getByPresenceListId(presenceListId));
    }

    // DELETE /api/appels/{id} — suppression d'un appel (révocation).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteAppelUC.execute(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/appels/{id}/mark — pour appel MANUEL : l'enseignant marque un étudiant présent.
    @PostMapping("/{id}/mark")
    public ResponseEntity<PresenceRowResponseDTO> markManuel(
            @PathVariable Long id,
            @RequestParam Long etudiantId) {
        return ResponseEntity.ok(markStudentPresentUC.execute(
                new MarkStudentPresentCommand(etudiantId, null, null, id)
        ));
    }

    // POST /api/appels/{id}/close — clôture l'appel.
    // Toutes les InfoPresenceRow isPresent=null passent à false (absents).
    @PostMapping("/{id}/close")
    public ResponseEntity<Map<String, Integer>> close(@PathVariable Long id) {
        int absentCount = closeAppelUC.execute(id);
        return ResponseEntity.ok(Map.of("absentCount", absentCount));
    }

    // POST /api/appels/presence-list/{presenceListId}/close-all — fin de séance :
    // clôture tous les appels encore en attente pour cette liste de présence, pour
    // qu'aucun créneau ne reste isPresent=null indéfiniment.
    @PostMapping("/presence-list/{presenceListId}/close-all")
    public ResponseEntity<Map<String, Integer>> closeAll(@PathVariable Long presenceListId) {
        int absentCount = closeAllPendingAppelsUC.execute(presenceListId);
        return ResponseEntity.ok(Map.of("absentCount", absentCount));
    }
}

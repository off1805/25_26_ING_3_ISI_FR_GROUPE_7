package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MarkStudentPresentUCImpl implements MarkStudentPresentUC {

    private final AttendanceCodeRepository attendanceCodeRepo;
    private final PresenceListRepository presenceListRepo;
    private final PresenceRowRepository presenceRowRepo;

    @Override
    public PresenceRowResponseDTO execute(MarkStudentPresentCommand command) {
        // 1. Récupérer et valider le code
        AttendanceCode code = attendanceCodeRepo.findById(command.idCode())
                .orElseThrow(() -> new IllegalArgumentException("Code introuvable : " + command.idCode()));

        if (code.isExpired()) {
            throw new IllegalStateException("Le code a expiré");
        }

        // 2. Récupérer la PresenceList liée à la séance
        List<PresenceList> lists = presenceListRepo.findBySeanceId(code.getSeanceId());
        if (lists.isEmpty()) {
            throw new IllegalStateException("Aucune liste de présence trouvée pour cette séance");
        }
        PresenceList presenceList = lists.get(0);

        // 3. Chercher la PresenceRow de l'étudiant
        PresenceRow row = presenceRowRepo.findByPresenceListId(presenceList.getId())
                .stream()
                .filter(r -> r.getEtudiantId().equals(command.idStudent()))
                .findFirst()
                .orElse(null);

        // 4. Mettre à jour ou créer la ligne
        if (row != null) {
            row.update(true, 0f);
        } else {
            row = new PresenceRow(presenceList.getId(), command.idStudent(), true);
        }

        return PresenceRowResponseDTO.fromDomain(presenceRowRepo.save(row));
    }
}

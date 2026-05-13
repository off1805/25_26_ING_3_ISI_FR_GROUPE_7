package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.Appel;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MarkStudentPresentUCImpl implements MarkStudentPresentUC {

    private final AttendanceCodeRepository attendanceCodeRepo;
    private final AppelRepository appelRepo;
    private final PresenceListRepository presenceListRepo;
    private final PresenceRowRepository presenceRowRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;

    @Override
    public PresenceRowResponseDTO execute(MarkStudentPresentCommand command) {
        Appel appel = resolveAppel(command);

        if (appel.isExpired()) {
            throw new IllegalStateException("L'appel a expiré");
        }

        List<PresenceList> lists = presenceListRepo.findById(appel.getPresenceListId())
                .map(List::of)
                .orElseThrow(() -> new IllegalStateException(
                    "Liste de présence introuvable pour l'appel " + appel.getId()));

        PresenceList presenceList = lists.get(0);

        // Upsert : si la ligne existe déjà (scan multiple), on remet à present=true sans doublon.
        PresenceRow row = presenceRowRepo.findByPresenceListId(presenceList.getId())
                .stream()
                .filter(r -> r.getEtudiantId().equals(command.idStudent()))
                .findFirst()
                .orElse(null);

        if (row != null) {
            row.update(true);
        } else {
            row = new PresenceRow(presenceList.getId(), command.idStudent(), true);
        }
        row = presenceRowRepo.save(row);

        // Upsert de l'InfoPresenceRow : met à jour la ligne null créée à l'ouverture de l'appel
        // ou en crée une nouvelle si elle n'existe pas (ex. QR scan sans liste pré-créée).
        InfoPresenceRow info = infoPresenceRowRepo
                .findByAppelIdAndEtudiantId(appel.getId(), command.idStudent())
                .orElse(null);
        if (info != null) {
            info.setPresenceRowId(row.getId());
            info.setIsPresent(true);
        } else {
            info = new InfoPresenceRow(
                    command.idStudent(), row.getId(), appel.getId(),
                    appel.getHeureDebut(), appel.getHeureFin(), true
            );
        }
        infoPresenceRowRepo.save(info);

        // Recalcule present depuis l'ensemble des InfoPresenceRow liées à cette PresenceRow.
        row.recalculatePresent(infoPresenceRowRepo.findByPresenceRowId(row.getId()));
        row = presenceRowRepo.save(row);

        return PresenceRowResponseDTO.fromDomain(row);
    }

    // Résout l'Appel selon la stratégie : MANUEL (appelId direct), QR (codeValeur), PIN (idCode).
    private Appel resolveAppel(MarkStudentPresentCommand command) {
        if (command.appelId() != null) {
            Appel appel = appelRepo.findById(command.appelId())
                    .orElseThrow(() -> new IllegalArgumentException("Appel introuvable : " + command.appelId()));
            if (!appel.isManuel()) {
                throw new IllegalArgumentException("Cet appel n'est pas de type MANUEL");
            }
            return appel;
        }

        AttendanceCode code;
        if (command.codeValeur() != null) {
            code = attendanceCodeRepo.findByValeur(command.codeValeur())
                    .orElseThrow(() -> new IllegalArgumentException("Code QR invalide"));
        } else {
            code = attendanceCodeRepo.findById(command.idCode())
                    .orElseThrow(() -> new IllegalArgumentException("Code introuvable : " + command.idCode()));
        }

        if (code.isExpired()) {
            throw new IllegalStateException("Le code a expiré");
        }

        return appelRepo.findByAttendanceCodeId(code.getId())
                .orElseThrow(() -> new IllegalStateException(
                    "Aucun appel trouvé pour le code " + code.getId()));
    }
}

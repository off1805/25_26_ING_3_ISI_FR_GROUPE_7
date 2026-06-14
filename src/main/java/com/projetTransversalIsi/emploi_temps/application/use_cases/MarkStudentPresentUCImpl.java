package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.PresenceNotificationService;
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
    private final PresenceNotificationService notificationService;

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

        // Marque présent tous les créneaux horaires (InfoPresenceRow) de cet étudiant qui
        // chevauchent la plage de cet appel — y compris ceux déjà clôturés en "absent" par
        // un appel antérieur (un scan tardif corrige une absence déjà enregistrée). Les
        // créneaux déjà "présent" ne sont pas réécrits.
        List<InfoPresenceRow> overlapping = infoPresenceRowRepo.findByPresenceRowId(row.getId())
                .stream()
                .filter(info -> Appel.overlaps(info.getHeureDebut(), info.getHeureFin(),
                                                 appel.getHeureDebut(), appel.getHeureFin()))
                .toList();

        if (overlapping.isEmpty()) {
            // Cas de repli (ex. QR scan sans créneaux pré-créés) : on crée un créneau
            // couvrant la plage de l'appel.
            infoPresenceRowRepo.save(new InfoPresenceRow(
                    command.idStudent(), row.getId(), appel.getId(),
                    appel.getHeureDebut(), appel.getHeureFin(), true
            ));
        } else {
            for (InfoPresenceRow info : overlapping) {
                if (!Boolean.TRUE.equals(info.getIsPresent())) {
                    info.setIsPresent(true);
                    info.setAppelId(appel.getId());
                    infoPresenceRowRepo.save(info);
                }
            }
        }

        // Recalcule present depuis l'ensemble des InfoPresenceRow liées à cette PresenceRow.
        row.recalculatePresent(infoPresenceRowRepo.findByPresenceRowId(row.getId()));
        row = presenceRowRepo.save(row);

        notificationService.notifyStudentPresent(command.idStudent(), presenceList.getId());

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
        System.out.println(command.codeValeur());
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

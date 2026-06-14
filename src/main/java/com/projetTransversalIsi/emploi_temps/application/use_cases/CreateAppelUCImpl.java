package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.AppelExpirationScheduler;
import com.projetTransversalIsi.emploi_temps.application.dto.AppelResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAppelDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.Appel;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreateAppelUCImpl implements CreateAppelUC {

    private final AppelRepository appelRepo;
    private final AttendanceCodeRepository attendanceCodeRepo;
    private final SeanceRepository seanceRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;
    private final PresenceRowRepository presenceRowRepo;
    private final AppelExpirationScheduler expirationScheduler;

    @Value("${server.domain:127.0.0.1}")
    private String serverDomain;

    @Override
    public AppelResponseDTO execute(CreateAppelDTO dto) {
        Seance seance = seanceRepo.findById(dto.seanceId())
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + dto.seanceId()));

        // Vérification que la plage de l'appel est incluse dans la plage de la séance.
        Appel tempAppel = new Appel();
        tempAppel.setHeureDebut(dto.heureDebut());
        tempAppel.setHeureFin(dto.heureFin());
        tempAppel.setTypeAppel(dto.typeAppel());
        tempAppel.validatePlageHoraire(seance.getHeureDebut(), seance.getHeureFin());

        String valeur = null;
        Long attendanceCodeId = null;

        if (dto.typeAppel() != Appel.TypeAppel.MANUEL) {
            // Pour QR/PIN : création du AttendanceCode sous-jacent.
            AttendanceCode.CodeType codeType = dto.typeAppel() == Appel.TypeAppel.QR
                    ? AttendanceCode.CodeType.QR
                    : AttendanceCode.CodeType.PIN;
            valeur = generateValeur(codeType);
            AttendanceCode code = new AttendanceCode(
                    dto.seanceId(), dto.enseignantId(), codeType,
                    valeur, 0f, dto.dureeVieMinutes()
            );
            AttendanceCode savedCode = attendanceCodeRepo.save(code);
            attendanceCodeId = savedCode.getId();
        }

        Appel appel = new Appel(
                dto.presenceListId(), dto.enseignantId(), dto.typeAppel(),
                valeur, attendanceCodeId,
                dto.heureDebut(), dto.heureFin(), dto.dureeVieMinutes()
        );

        String baseUrl = "https://" + serverDomain + "/api/presences/scan?code=";
        Appel savedAppel = appelRepo.save(appel);

        // Pour chaque étudiant fourni : upsert PresenceRow (null), puis InfoPresenceRow.
        // Le nombre d'InfoPresenceRow par PresenceRow doit toujours égaler le nombre
        // d'heures de la séance : on les crée donc UNE SEULE FOIS (au premier appel),
        // un par créneau horaire. Les appels suivants ne créent rien : ils "réclament"
        // (appel_id) les créneaux encore en attente qu'ils couvrent.
        if (dto.etudiantIds() != null && !dto.etudiantIds().isEmpty()) {
            List<PresenceRow> existingRows = presenceRowRepo.findByPresenceListId(dto.presenceListId());
            Map<Long, PresenceRow> byEtudiant = existingRows.stream()
                    .collect(Collectors.toMap(PresenceRow::getEtudiantId, r -> r));

            List<LocalTime[]> hourSlots = hourSlots(seance.getHeureDebut(), seance.getHeureFin());

            for (Long etudiantId : dto.etudiantIds()) {
                // PresenceRow créée avec present=null : statut non encore déterminé.
                PresenceRow presenceRow = byEtudiant.computeIfAbsent(etudiantId,
                        id -> presenceRowRepo.save(new PresenceRow(dto.presenceListId(), id, null)));

                List<InfoPresenceRow> existingInfo = infoPresenceRowRepo.findByPresenceRowId(presenceRow.getId());

                if (existingInfo.isEmpty()) {
                    // Premier appel pour cet étudiant sur cette séance : un créneau par
                    // heure de cours, couvrant la séance entière.
                    for (LocalTime[] slot : hourSlots) {
                        infoPresenceRowRepo.save(new InfoPresenceRow(
                                etudiantId, presenceRow.getId(), savedAppel.getId(),
                                slot[0], slot[1]
                        ));
                    }
                } else {
                    // Appels suivants : on réclame les créneaux encore en attente que
                    // ce nouvel appel couvre, sans en créer de nouveaux.
                    for (InfoPresenceRow info : existingInfo) {
                        if (info.getIsPresent() == null
                                && Appel.overlaps(info.getHeureDebut(), info.getHeureFin(),
                                                   dto.heureDebut(), dto.heureFin())) {
                            info.setAppelId(savedAppel.getId());
                            infoPresenceRowRepo.save(info);
                        }
                    }
                }
            }
        }

        expirationScheduler.scheduleClose(savedAppel);

        return AppelResponseDTO.fromDomain(savedAppel, baseUrl);
    }

    // Découpe [debut, fin) en créneaux d'une heure (le dernier créneau peut être plus court).
    private List<LocalTime[]> hourSlots(LocalTime debut, LocalTime fin) {
        List<LocalTime[]> slots = new ArrayList<>();
        LocalTime cursor = debut;
        while (cursor.isBefore(fin)) {
            LocalTime next = cursor.plusHours(1);
            if (next.isAfter(fin)) {
                next = fin;
            }
            slots.add(new LocalTime[]{cursor, next});
            cursor = next;
        }
        return slots;
    }

    private String generateValeur(AttendanceCode.CodeType type) {
        if (type == AttendanceCode.CodeType.PIN) {
            return String.format("%06d", new Random().nextInt(999999));
        }
        return UUID.randomUUID().toString();
    }
}

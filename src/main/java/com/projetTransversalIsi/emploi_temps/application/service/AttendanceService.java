package com.projetTransversalIsi.emploi_temps.application.service;

import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository.SpringDataEmploiTempsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceCodeRepository codeRepo;
    private final PresenceListRepository presenceListRepo;
    private final PresenceRowRepository presenceRowRepo;
    private final SpringDataEmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;
    private final OffreUeRepository offreUeRepo;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public AttendanceCode launchAttendance(CreateAttendanceCodeDTO request) {
        // Supprimer les anciens codes pour cette séance/enseignant si nécessaire
        List<AttendanceCode> existing = codeRepo.findBySeanceId(request.seanceId());
        for (AttendanceCode old : existing) {
            if (old.getEnseignantId().equals(request.enseignantId())) {
                codeRepo.delete(old.getId());
            }
        }

        String codeVal;
        if (request.type() == AttendanceCode.CodeType.PIN) {
            codeVal = String.format("%04d", random.nextInt(10000));
        } else {
            codeVal = generateRandomString(12);
        }

        AttendanceCode code = new AttendanceCode(
                request.seanceId(),
                request.enseignantId(),
                request.type(),
                codeVal,
                request.heuresAMarquer(),
                request.dureeVieMinutes()
        );

        // Immédiatement créer/récupérer une liste de présence pour PIN/QR
        employmentTempsBySeance(request.seanceId()).ifPresent(emploi -> {
            Optional<PresenceList> existingList = presenceListRepo.findBySeanceId(request.seanceId()).stream().findFirst();
            if (existingList.isPresent()) {
                PresenceList list = existingList.get();
                list.setHeuresMarquer(list.getHeuresMarquer() + request.heuresAMarquer());
                presenceListRepo.save(list);
            } else {
                Long ueId = getUeIdForSeance(request.seanceId());
                PresenceList list = new PresenceList(request.seanceId(), emploi.getClasseId(), ueId, request.enseignantId(), LocalDate.now(), request.heuresAMarquer());
                presenceListRepo.save(list);
            }
        });

        return codeRepo.save(code);
    }

    @Transactional
    public PresenceList submitManualAttendance(Long seanceId, Long enseignantId, List<Long> presentStudentIds, List<Long> allStudentIds, float hoursToMark) {
        return employmentTempsBySeance(seanceId).map(emploi -> {
            Long classeId = emploi.getClasseId();
            
            // Trouver ou créer la liste
            Optional<PresenceList> existingList = presenceListRepo.findBySeanceId(seanceId).stream().findFirst();
            PresenceList list;
            if (existingList.isPresent()) {
                list = existingList.get();
                list.setHeuresMarquer(list.getHeuresMarquer() + hoursToMark);
            } else {
                Long ueId = getUeIdForSeance(seanceId);
                list = new PresenceList(seanceId, classeId, ueId, enseignantId, LocalDate.now(), hoursToMark);
            }
            PresenceList saved = presenceListRepo.save(list);
            
            for (Long studentId : allStudentIds) {
                boolean isPresent = presentStudentIds.contains(studentId);
                
                Optional<PresenceRow> existingRow = presenceRowRepo.findByPresenceListIdAndEtudiantId(saved.getId(), studentId);
                if (existingRow.isPresent()) {
                    PresenceRow row = existingRow.get();
                    row.setPresent(isPresent);
                    if (!isPresent) {
                        row.setHeuresAbsence(row.getHeuresAbsence() + hoursToMark);
                    }
                    presenceRowRepo.save(row);
                } else {
                    PresenceRow row = new PresenceRow(saved.getId(), studentId, isPresent);
                    if (!isPresent) {
                        row.setHeuresAbsence(hoursToMark);
                    }
                    presenceRowRepo.save(row);
                }
            }
            
            return saved;
        }).orElseThrow(() -> new RuntimeException("Emploi du temps non trouvé pour la séance " + seanceId));
    }

    private String generateRandomString(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private Optional<com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaEmploiTempsEntity> employmentTempsBySeance(Long seanceId) {
        return emploiTempsRepo.findBySeanceId(seanceId);
    }

    private Long getUeIdForSeance(Long seanceId) {
        return seanceRepo.findById(seanceId)
                .flatMap(seance -> {
                    if (seance.getCoursId() != null) {
                        return offreUeRepo.findById(seance.getCoursId()).map(OffreUe::getUeId);
                    }
                    return Optional.empty();
                })
                .orElseThrow(() -> new RuntimeException("Impossible de trouver l'UE pour la séance " + seanceId));
    }
}

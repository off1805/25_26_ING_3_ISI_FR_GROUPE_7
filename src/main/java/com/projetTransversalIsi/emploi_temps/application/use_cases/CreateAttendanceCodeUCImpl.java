package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateAttendanceCodeUCImpl implements CreateAttendanceCodeUC {

    private final AttendanceCodeRepository attendanceCodeRepo;

    @Override
    public AttendanceCodeResponseDTO execute(CreateAttendanceCodeDTO dto) {
        String valeur = generateValeur(dto.type());
        AttendanceCode code = new AttendanceCode(
                dto.seanceId(), dto.enseignantId(), dto.type(),
                valeur, dto.heuresAMarquer(), dto.dureeVieMinutes()
        );
        return AttendanceCodeResponseDTO.fromDomain(attendanceCodeRepo.save(code));
    }

    private String generateValeur(AttendanceCode.CodeType type) {
        if (type == AttendanceCode.CodeType.PIN) {
            // PIN à 6 chiffres
            return String.format("%06d", new Random().nextInt(999999));
        }
        // QR : UUID complet
        return UUID.randomUUID().toString();
    }
}

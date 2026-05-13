package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateAttendanceCodeUCImpl implements CreateAttendanceCodeUC {

    private final AttendanceCodeRepository attendanceCodeRepo;

    @Value("${app.ip_address:127.0.0.1}")
    private String ipAddress;

    @Value("${server.port:8080}")
    private String serverPort;
    // serverDomain utilisé en prod ; ipAddress:serverPort gardé en commentaire pour le dev local.
    @Value("${server.domain:127.0.0.1}")
    private String serverDomain;

    @Override
    public AttendanceCodeResponseDTO execute(CreateAttendanceCodeDTO dto) {
        String valeur = generateValeur(dto.type());
        AttendanceCode code = new AttendanceCode(
                dto.seanceId(), dto.enseignantId(), dto.type(),
                valeur, dto.heuresAMarquer(), dto.dureeVieMinutes()
        );
        // L'URL de scan est construite côté serveur pour que le QR code embarque l'adresse publique.
        // Seul le type QR génère une URL (PIN est saisi manuellement, pas scanné).
       // String baseUrl = "https://" +serverDomain+  "/api/presences/scan?code=";
        String baseUrl = "https://" + ipAddress + ":" + serverPort + "/api/presences/scan?code=";
        return AttendanceCodeResponseDTO.fromDomain(attendanceCodeRepo.save(code), baseUrl);
    }

    private String generateValeur(AttendanceCode.CodeType type) {
        if (type == AttendanceCode.CodeType.PIN) {
            // PIN : 6 chiffres avec zéros de tête (ex: 007823) pour garantir la longueur fixe.
            return String.format("%06d", new Random().nextInt(999999));
        }
        // QR : UUID aléatoire ; unicité garantie par la probabilité UUID v4 et contrainte DB unique.
        return UUID.randomUUID().toString();
    }
}

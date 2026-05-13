package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAttendanceCodeUCImpl implements GetAttendanceCodeUC {

    private final AttendanceCodeRepository attendanceCodeRepo;

    @Value("${app.ip_address:127.0.0.1}")
    private String ipAddress;

    @Value("${server.port:8080}")
    private String serverPort;

    // Attention : buildBaseUrl utilise http (non sécurisé) alors que CreateAttendanceCodeUCImpl
    // utilise https. À aligner si le frontend vérifie l'URL lors de la génération du QR code.
    private String buildBaseUrl() {
        return "http://" + ipAddress + ":" + serverPort + "/api/presences/scan?code=";
    }

    @Override
    public AttendanceCodeResponseDTO getById(Long id) {
        String baseUrl = buildBaseUrl();
        return attendanceCodeRepo.findById(id)
                .map(c -> AttendanceCodeResponseDTO.fromDomain(c, baseUrl))
                .orElseThrow(() -> new IllegalArgumentException("Code introuvable : " + id));
    }

    // Retourne tous les codes d'une séance (QR et PIN) pour que le frontend puisse
    // afficher lequel est actif ou expiré via le champ `expired` du DTO.
    @Override
    public List<AttendanceCodeResponseDTO> getBySeanceId(Long seanceId) {
        String baseUrl = buildBaseUrl();
        return attendanceCodeRepo.findBySeanceId(seanceId)
                .stream()
                .map(c -> AttendanceCodeResponseDTO.fromDomain(c, baseUrl))
                .collect(Collectors.toList());
    }
}

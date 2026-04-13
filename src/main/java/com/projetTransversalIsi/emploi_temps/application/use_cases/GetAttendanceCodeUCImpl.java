package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAttendanceCodeUCImpl implements GetAttendanceCodeUC {

    private final AttendanceCodeRepository attendanceCodeRepo;

    @Override
    public AttendanceCodeResponseDTO getById(Long id) {
        return attendanceCodeRepo.findById(id)
                .map(AttendanceCodeResponseDTO::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException("Code introuvable : " + id));
    }

    @Override
    public List<AttendanceCodeResponseDTO> getBySeanceId(Long seanceId) {
        return attendanceCodeRepo.findBySeanceId(seanceId)
                .stream()
                .map(AttendanceCodeResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}

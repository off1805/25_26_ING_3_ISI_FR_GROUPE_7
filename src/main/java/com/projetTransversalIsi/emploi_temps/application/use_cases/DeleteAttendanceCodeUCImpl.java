package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.domain.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAttendanceCodeUCImpl implements DeleteAttendanceCodeUC {

    private final AttendanceCodeRepository attendanceCodeRepo;

    @Override
    public void execute(Long id) {
        attendanceCodeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Code introuvable : " + id));
        attendanceCodeRepo.delete(id);
    }
}

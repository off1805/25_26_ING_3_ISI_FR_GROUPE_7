package com.projetTransversalIsi.web_application.teacher;

import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;
import com.projetTransversalIsi.emploi_temps.application.service.AttendanceService;
import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/launch")
    public ResponseEntity<AttendanceCode> launchAttendance(@RequestBody CreateAttendanceCodeDTO request) {
        AttendanceCode code = attendanceService.launchAttendance(request);
        return ResponseEntity.ok(code);
    }

    @PostMapping("/submit-manual")
    public ResponseEntity<Void> submitManual(@RequestBody ManualSubmissionRequest request) {
        attendanceService.submitManualAttendance(
                request.seanceId(),
                request.enseignantId(),
                request.presentStudentIds(),
                request.allStudentIds(),
                request.hoursToMark()
        );
        return ResponseEntity.ok().build();
    }

    public record ManualSubmissionRequest(
            Long seanceId,
            Long enseignantId,
            List<Long> presentStudentIds,
            List<Long> allStudentIds,
            float hoursToMark
    ) {}
}

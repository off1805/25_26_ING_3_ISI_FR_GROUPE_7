package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.AttendanceCodeResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.CreateAttendanceCodeDTO;
import com.projetTransversalIsi.emploi_temps.application.use_cases.CreateAttendanceCodeUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.DeleteAttendanceCodeUC;
import com.projetTransversalIsi.emploi_temps.application.use_cases.GetAttendanceCodeUC;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance-codes")
@RequiredArgsConstructor
public class AttendanceCodeController {

    private final CreateAttendanceCodeUC createAttendanceCodeUC;
    private final GetAttendanceCodeUC getAttendanceCodeUC;
    private final DeleteAttendanceCodeUC deleteAttendanceCodeUC;

    @PostMapping
    public ResponseEntity<AttendanceCodeResponseDTO> create(@RequestBody CreateAttendanceCodeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createAttendanceCodeUC.execute(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceCodeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getAttendanceCodeUC.getById(id));
    }

    @GetMapping("/seance/{seanceId}")
    public ResponseEntity<List<AttendanceCodeResponseDTO>> getBySeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(getAttendanceCodeUC.getBySeanceId(seanceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteAttendanceCodeUC.execute(id);
        return ResponseEntity.noContent().build();
    }
}

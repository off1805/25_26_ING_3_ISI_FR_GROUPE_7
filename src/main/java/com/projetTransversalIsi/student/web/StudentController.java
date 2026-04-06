package com.projetTransversalIsi.student.web;

import com.projetTransversalIsi.student.application.dto.EnrollStudentRequestDTO;
import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;
import com.projetTransversalIsi.student.application.use_cases.EnrollStudentUC;
import com.projetTransversalIsi.student.application.use_cases.RemoveStudentFromClasseUC;
import com.projetTransversalIsi.student.domain.exceptions.StudentAlreadyEnrolledException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final EnrollStudentUC enrollStudentUC;
    private final RemoveStudentFromClasseUC removeStudentFromClasseUC;

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@Valid @RequestBody EnrollStudentRequestDTO request) {
        log.info("Inscription étudiant : {}", request.email());
        try {
            EnrollStudentResponseDTO response = enrollStudentUC.execute(request);
            HttpStatus status = response.created() ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(response);
        } catch (StudentAlreadyEnrolledException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/classes/{classeId}")
    public ResponseEntity<?> removeFromClasse(
            @PathVariable Long userId,
            @PathVariable Long classeId) {
        try {
            removeStudentFromClasseUC.execute(userId, classeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
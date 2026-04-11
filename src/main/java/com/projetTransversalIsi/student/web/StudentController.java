package com.projetTransversalIsi.student.web;

import com.projetTransversalIsi.student.application.dto.EnrollStudentRequestDTO;
import com.projetTransversalIsi.student.application.dto.EnrollStudentResponseDTO;
import com.projetTransversalIsi.student.application.dto.FilterStudent;
import com.projetTransversalIsi.student.application.use_cases.EnrollStudentUC;
import com.projetTransversalIsi.user.services.GetAllStudent;
import com.projetTransversalIsi.student.application.use_cases.RemoveStudentFromClasseUC;
import com.projetTransversalIsi.student.domain.exceptions.StudentAlreadyEnrolledException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final GetAllStudent getStudent;

    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@Valid @RequestBody EnrollStudentRequestDTO request) {
        log.info("Inscription étudiant : {}", request.email());
        try {
            EnrollStudentResponseDTO response = enrollStudentUC.execute(request);
            HttpStatus status =  HttpStatus.OK;
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


    @GetMapping("/classes/{classeId}")
    public ResponseEntity<Page<EnrollStudentResponseDTO>> getStudentFromClass(@PathVariable("classeId") Long classId, @PageableDefault(size=10) Pageable page){
        return ResponseEntity.ok(getStudent.execute(FilterStudent.builder().classeId(classId).build(),page));
    }
}
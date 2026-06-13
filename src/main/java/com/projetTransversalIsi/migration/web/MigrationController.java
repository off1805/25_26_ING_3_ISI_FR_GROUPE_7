package com.projetTransversalIsi.migration.web;

import com.projetTransversalIsi.migration.application.dto.CreateMigrationRequestDTO;
import com.projetTransversalIsi.migration.application.dto.UpdateMigrationRequestDTO;
import com.projetTransversalIsi.migration.application.use_cases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/migrations")
@RequiredArgsConstructor
public class MigrationController {

    private final CreateMigrationUC createMigrationUC;
    private final CancelMigrationUC cancelMigrationUC;
    private final UpdateMigrationUC updateMigrationUC;
    private final ListPendingMigrationsUC listPendingMigrationsUC;
    private final GetEligibleDestinationClassesUC getEligibleDestinationClassesUC;
    private final CheckGraduationEligibilityUC checkGraduationEligibilityUC;
    private final ArchiveStudentUC archiveStudentUC;
    private final ExpelStudentUC expelStudentUC;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateMigrationRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(createMigrationUC.execute(request));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<?>> pending(@RequestParam Long filiereId) {
        return ResponseEntity.ok(listPendingMigrationsUC.execute(filiereId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateMigrationRequestDTO request) {
        try {
            return ResponseEntity.ok(updateMigrationUC.execute(id, request));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            cancelMigrationUC.execute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/eligible-classes")
    public ResponseEntity<?> eligibleClasses(@RequestParam Long classeSourceId) {
        try {
            return ResponseEntity.ok(getEligibleDestinationClassesUC.execute(classeSourceId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/classes/{classeId}/graduation-eligible")
    public ResponseEntity<?> graduationEligible(@PathVariable Long classeId) {
        try {
            return ResponseEntity.ok(checkGraduationEligibilityUC.execute(classeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/students/{userId}/archive")
    public ResponseEntity<?> archive(@PathVariable Long userId) {
        try {
            archiveStudentUC.execute(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/students/{userId}/expel", consumes = "multipart/form-data")
    public ResponseEntity<?> expel(
            @PathVariable Long userId,
            @RequestParam String motif,
            @RequestParam MultipartFile justificatif) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(expelStudentUC.execute(userId, motif, justificatif));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

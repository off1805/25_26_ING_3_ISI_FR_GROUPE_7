package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.application.use_cases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presences")
@RequiredArgsConstructor
public class PresenceController {

    private final CreatePresenceListUC createPresenceListUC;
    private final GetPresenceListUC getPresenceListUC;
    private final DeletePresenceListUC deletePresenceListUC;
    private final AddPresenceRowUC addPresenceRowUC;
    private final UpdatePresenceRowUC updatePresenceRowUC;

    // PresenceList

    @PostMapping
    public ResponseEntity<PresenceListResponseDTO> create(@RequestBody CreatePresenceListDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPresenceListUC.execute(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresenceListResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getPresenceListUC.getById(id));
    }

    @GetMapping("/{id}/rows")
    public ResponseEntity<List<PresenceRowResponseDTO>> getRows(@PathVariable Long id) {
        return ResponseEntity.ok(getPresenceListUC.getRows(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePresenceListUC.execute(id);
        return ResponseEntity.noContent().build();
    }

    // PresenceRow

    @PostMapping("/rows")
    public ResponseEntity<PresenceRowResponseDTO> addRow(@RequestBody CreatePresenceRowDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addPresenceRowUC.execute(dto));
    }

    @PutMapping("/rows")
    public ResponseEntity<PresenceRowResponseDTO> updateRow(@RequestBody UpdatePresenceRowDTO dto) {
        return ResponseEntity.ok(updatePresenceRowUC.execute(dto));
    }
}

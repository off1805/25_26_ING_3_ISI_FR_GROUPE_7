package com.projetTransversalIsi.security.web;

import com.projetTransversalIsi.security.application.use_cases.FindPermissionsByRoleUC;
import com.projetTransversalIsi.user.application.dto.PermissionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final FindPermissionsByRoleUC findPermissionsByRoleUC;

    @GetMapping("/{roleName}/permissions")
    public ResponseEntity<Set<PermissionResponseDTO>> getPermissionsByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(
                findPermissionsByRoleUC.execute(roleName).stream()
                        .map(PermissionResponseDTO::fromPermissionDomain)
                        .collect(Collectors.toSet()));
    }
}

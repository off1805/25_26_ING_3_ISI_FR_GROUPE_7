package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AppelResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAppelUCImpl implements GetAppelUC {

    private final AppelRepository appelRepo;

    @Value("${server.domain:127.0.0.1}")
    private String serverDomain;

    private String buildBaseUrl() {
        return "https://" + serverDomain + "/api/presences/scan?code=";
    }

    @Override
    public AppelResponseDTO getById(Long id) {
        return appelRepo.findById(id)
                .map(a -> AppelResponseDTO.fromDomain(a, buildBaseUrl()))
                .orElseThrow(() -> new IllegalArgumentException("Appel introuvable : " + id));
    }

    @Override
    public List<AppelResponseDTO> getByPresenceListId(Long presenceListId) {
        return appelRepo.findByPresenceListId(presenceListId).stream()
                .map(a -> AppelResponseDTO.fromDomain(a, buildBaseUrl()))
                .collect(Collectors.toList());
    }
}

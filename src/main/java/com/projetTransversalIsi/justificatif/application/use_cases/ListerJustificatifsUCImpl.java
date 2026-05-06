package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListerJustificatifsUCImpl implements ListerJustificatifsUC {

    private final JustificatifRepository justificatifRepo;

    @Override
    public List<JustificatifResponseDTO> getByEtudiant(Long etudiantId) {
        return justificatifRepo.findByEtudiantId(etudiantId)
                .stream().map(JustificatifResponseDTO::fromDomain).collect(Collectors.toList());
    }

    @Override
    public List<JustificatifResponseDTO> getAll() {
        return justificatifRepo.findAll()
                .stream().map(JustificatifResponseDTO::fromDomain).collect(Collectors.toList());
    }
}

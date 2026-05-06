package com.projetTransversalIsi.justificatif.application.use_cases;

import com.projetTransversalIsi.justificatif.application.dto.DecisionJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RejeterJustificatifUCImpl implements RejeterJustificatifUC {

    private final JustificatifRepository justificatifRepo;

    @Override
    public JustificatifResponseDTO execute(DecisionJustificatifDTO dto) {
        Justificatif j = justificatifRepo.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Justificatif introuvable : " + dto.id()));
        j.rejeter(dto.commentaire());
        return JustificatifResponseDTO.fromDomain(justificatifRepo.save(j));
    }
}

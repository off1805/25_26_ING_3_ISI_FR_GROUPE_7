package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.domain.UeRepository;
import com.projetTransversalIsi.pedagogie.application.dto.UeFiltreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchUeUCImpl implements SearchUeUC {

    private final UeRepository ueRepository;

    @Override
    public Page<Ue> execute(UeFiltreDto command, Pageable pageable) {
        return ueRepository.findAll(command, pageable);
    }
}

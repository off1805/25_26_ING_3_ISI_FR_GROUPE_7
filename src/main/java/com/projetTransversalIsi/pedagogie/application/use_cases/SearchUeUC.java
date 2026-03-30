package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.Ue;
import com.projetTransversalIsi.pedagogie.application.dto.UeFiltreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUeUC {
    Page<Ue> execute(UeFiltreDto command, Pageable pageable);
}

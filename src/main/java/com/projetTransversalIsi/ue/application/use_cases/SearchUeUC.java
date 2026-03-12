package com.projetTransversalIsi.ue.application.use_cases;

import com.projetTransversalIsi.ue.domain.Ue;
import com.projetTransversalIsi.ue.application.dto.UeFiltreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUeUC {
    Page<Ue> execute(UeFiltreDto command, Pageable pageable);
}

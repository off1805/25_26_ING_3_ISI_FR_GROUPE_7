package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import java.util.List;

public interface SearchNiveauxUC {
    List<Niveau> execute(SearchNiveauRequestDTO request);
}

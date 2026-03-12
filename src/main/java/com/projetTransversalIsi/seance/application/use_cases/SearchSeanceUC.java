package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.SearchSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;
import java.util.List;

public interface SearchSeanceUC {
    List<Seance> execute(SearchSeanceRequestDTO criteria);
}
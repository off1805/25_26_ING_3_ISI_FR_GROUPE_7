package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import java.util.List;
public interface SearchFiliereUC {
    List<Filiere> execute(SearchFiliereRequestDTO criteria);
}

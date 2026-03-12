package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchNiveauxUCImpl implements SearchNiveauxUC {

    private final NiveauRepository niveauRepo;

    @Override
    public List<Niveau> execute(SearchNiveauRequestDTO request) {
        return niveauRepo.findAll(request);
    }
}

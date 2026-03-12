package com.projetTransversalIsi.ue.domain;

import com.projetTransversalIsi.ue.application.dto.UeFiltreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UeRepository {
    boolean ueAlreadyExists(String code);

    Ue save(Ue ue);

    Optional<Ue> findById(Long id);

    void delete(Ue ue);

    Page<Ue> findAll(UeFiltreDto command, Pageable pageable);
}

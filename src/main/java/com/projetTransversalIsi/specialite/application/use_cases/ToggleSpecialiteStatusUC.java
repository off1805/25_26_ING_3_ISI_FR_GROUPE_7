package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;

public interface ToggleSpecialiteStatusUC {
    Specialite execute(Long id, boolean activer);
}

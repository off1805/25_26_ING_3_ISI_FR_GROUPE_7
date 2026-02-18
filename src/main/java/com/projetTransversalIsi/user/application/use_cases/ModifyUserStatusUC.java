package com.projetTransversalIsi.user.application.use_cases;

import com.projetTransversalIsi.user.application.dto.ModifyUserStatusDTO;
import com.projetTransversalIsi.user.domain.User;

public interface ModifyUserStatusUC {
    User execute(Long id, ModifyUserStatusDTO command);
}

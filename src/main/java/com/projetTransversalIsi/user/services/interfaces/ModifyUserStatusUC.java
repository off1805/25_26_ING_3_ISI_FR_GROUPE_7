package com.projetTransversalIsi.user.services.interfaces;

import com.projetTransversalIsi.user.dto.ModifyUserStatusDTO;
import com.projetTransversalIsi.user.domain.User;

public interface ModifyUserStatusUC {
    User execute(Long id, ModifyUserStatusDTO command);
}

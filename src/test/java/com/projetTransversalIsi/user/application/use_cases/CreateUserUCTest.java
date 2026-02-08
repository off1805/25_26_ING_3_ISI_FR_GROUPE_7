package com.projetTransversalIsi.user.application.use_cases;


import com.projetTransversalIsi.security.application.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.application.services.FindRoleByIdAccessPort;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUCTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private FindRoleByIdAccessPort getRoleById;

    @Mock
    private FindAllPermissionByIdsAccessPort getAllPermById;

    @InjectMocks
    private CreateUserUCImpl createUserUC;

    @Test
    @DisplayName("Devrait creer un utilisateur avec succes")
    void shouldCreateUserSuccessfully(){
        //donnees
        String email = "test@exemple.com";
        String pass = "password123";
        Long roleId = 1L;
        Set<Long> permIds = Set.of(1L, 2L);

        when(userRepo.userAlreadyExists(email)).thenReturn(false);
        when(getRoleById.execute(roleId)).thenReturn(new Role(roleId, "ADMIN"));
        when(getAllPermById.execute(permIds)).thenReturn(Set.of(new Permission(1L, "READ","juste ecrire un truc")));

        User result = createUserUC.execute(email, pass, roleId, permIds);

        // 3. THEN (Vérification)
        assertNotNull(result);
        assertEquals(email, result.getEmail());

        // On vérifie que le repository a bien été appelé pour sauvegarder
        verify(userRepo, times(1)).save(any(User.class), eq(pass), anySet());


    }

    @Test
    @DisplayName("Devrait retourner null si l'utilisateur existe déjà")
    void shouldReturnNullWhenUserExists() {
        // GIVEN
        String email = "existant@exemple.com";
        when(userRepo.userAlreadyExists(email)).thenReturn(true);

        // WHEN
        User result = createUserUC.execute(email, "pass", 1L, Set.of());

        // THEN
        assertNull(result);
        // On vérifie qu'on n'a PAS essayé de chercher le rôle ou de sauvegarder
        verify(getRoleById, never()).execute(any());
        verify(userRepo, never()).save(any(), any(), any());
    }

}

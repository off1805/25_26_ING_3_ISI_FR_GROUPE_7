package com.projetTransversalIsi.user.application.use_cases;


import com.projetTransversalIsi.profil.application.services.InitProfile;
import com.projetTransversalIsi.profil.application.services.ProfileSelectionStrategy;
import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.security.application.services.FindAllPermissionByIdsAccessPort;
import com.projetTransversalIsi.security.application.services.FindRoleByIdAccessPort;
import com.projetTransversalIsi.security.application.services.PasswordHasherAC;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.UserRepository;
import com.projetTransversalIsi.user.domain.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUCTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private InitProfile initProfile;

    @Mock
    private ProfileSelectionStrategy profileSelectionStrategy;

    @Mock
    private FindRoleByIdAccessPort getRoleById;

    @Mock
    private FindAllPermissionByIdsAccessPort getAllPermById;

    @Mock
    private PasswordHasherAC passwordHasher;

    @InjectMocks
    private CreateUserUCImpl createUserUC;

    @Test
    @DisplayName("Devrait creer un utilisateur avec succes")
    void shouldCreateUserSuccessfully(){
        //donnees
        String email = "test@exemple.com";
        String pass = "password123";
        String roleId = "ADMIN";
        Set<String> permIds = Set.of("READ", "WRITE");
        CreateUserRequestDTO command = new CreateUserRequestDTO(email, pass, roleId, permIds);

        when(userRepo.userAlreadyExists(email)).thenReturn(false);
        when(passwordHasher.encode(pass)).thenReturn("hashed");
        Role role = new Role("ADMIN");
        when(getRoleById.findRoleById(roleId)).thenReturn(role);
        Profile selectedProfile = mock(Profile.class);
        Profile savedProfile = mock(Profile.class);
        when(profileSelectionStrategy.selectProfileFor(role)).thenReturn(selectedProfile);
        when(initProfile.execute(selectedProfile)).thenReturn(savedProfile);
        when(getAllPermById.findAllPermissionByIds(permIds)).thenReturn(Set.of(new Permission("READ", "juste ecrire un truc")));
        when(userRepo.registerNewUser(any(User.class), eq("hashed"), anySet(), eq(savedProfile)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = createUserUC.execute(command);

        // 3. THEN (Vérification)
        assertNotNull(result);
        assertEquals(email, result.getEmail());

        // On vérifie que le repository a bien été appelé pour sauvegarder
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).registerNewUser(userCaptor.capture(), eq("hashed"), anySet(), eq(savedProfile));
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals(role, userCaptor.getValue().getRole());


    }

    @Test
    @DisplayName("Devrait lever une exception si l'utilisateur existe déjà")
    void shouldThrowWhenUserExists() {
        // GIVEN
        String email = "existant@exemple.com";
        CreateUserRequestDTO command = new CreateUserRequestDTO(email, "password123", "ADMIN", Set.of());
        when(userRepo.userAlreadyExists(email)).thenReturn(true);

        // WHEN
        assertThrows(UserAlreadyExistsException.class, () -> createUserUC.execute(command));
        verify(getRoleById, never()).findRoleById(any());
        verify(userRepo, never()).save(any(), any(), any(), any());
    }

}

package com.projetTransversalIsi.user.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.application.use_cases.CreateUserUC;
import com.projetTransversalIsi.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) // Test uniquement la couche Web
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Pour simuler les requêtes HTTP

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets en JSON

    @MockBean
    private CreateUserUC createUserUC; // Simule le Use Case (car Spring en a besoin pour le contexte)

    @Test
    @DisplayName("Devrait retourner 201 et l'utilisateur créé")
    void shouldCreateUserAndReturn201() throws Exception {
        // 1. GIVEN (Préparation)
        String email = "test@isi.sn";
        CreateUserRequestDTO request = new CreateUserRequestDTO(
                email, "pass123", "ADMIN", Set.of("CREATE")
        );

        // On simule le retour du Use Case
        User userCreated = new User(email,new Role("ADMIN"));
        // Note : Si ton domaine User a besoin d'un ID ou d'un Role, assure-toi de les setter ici

        when(createUserUC.execute(eq(email), anyString(), eq("ADMIN"), anySet()))
                .thenReturn(userCreated);

        // 2. WHEN & THEN (Action et Vérification)
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convertit le DTO en JSON
                .andExpect(status().isCreated()) // Vérifie le code HTTP 201
                .andExpect(jsonPath("$.email").value(email)); // Vérifie le contenu du JSON de réponse
    }
}
package com.projetTransversalIsi.authentification.web;


import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;
import com.projetTransversalIsi.authentification.application.service.LoginUC;
import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.service.LogoutUC;
import com.projetTransversalIsi.authentification.application.service.RefreshTokenUC;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class AuthentificationController {

    private final LoginUC loginUc;
    private final RefreshTokenUC refreshTokenUC;
    private final LogoutUC logout;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  HttpServletResponse response) {
        LoginResponseDTO tokenAndRToken = loginUc.execute(request);
        setJwtCookie(response, tokenAndRToken.getToken());
        return ResponseEntity.ok(tokenAndRToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request,
                                                    HttpServletResponse response) {
        LoginResponseDTO tokenAndRToken = refreshTokenUC.execute(request);
        setJwtCookie(response, tokenAndRToken.getToken());
        return ResponseEntity.ok(tokenAndRToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO request,
                                       HttpServletResponse response) {
        logout.execute(request);
        clearJwtCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        String cookie = "access_token=" + token
                + "; HttpOnly"
                + "; Path=/"
                + "; SameSite=Lax"
                + "; Max-Age=" + (jwtExpirationMs / 1000);
        response.addHeader("Set-Cookie", cookie);
    }

    private void clearJwtCookie(HttpServletResponse response) {
        String cookie = "access_token="
                + "; HttpOnly"
                + "; Path=/"
                + "; SameSite=Lax"
                + "; Max-Age=0";
        response.addHeader("Set-Cookie", cookie);
    }

}

package com.projetTransversalIsi.security.application;

import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.authentification.application.exceptions.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthentificationFIlter extends OncePerRequestFilter {
    private final JwtService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse  response,
                                    FilterChain filterChain)
        throws ServletException, IOException{
        String header= request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer ")){
            String token= header.substring(7);
            try{
                Long userId=tokenService.getUserIdFromJwt(token);

                UsernamePasswordAuthenticationToken auth=
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch (InvalidTokenException e){
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request,response);
    }


}

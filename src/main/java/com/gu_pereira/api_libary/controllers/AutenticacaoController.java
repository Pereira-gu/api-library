package com.gu_pereira.api_libary.controllers;

import com.gu_pereira.api_libary.dto.LoginRequestDTO;
import com.gu_pereira.api_libary.infrastructure.TokenService;
import com.gu_pereira.api_libary.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid LoginRequestDTO data) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
            var authentication = manager.authenticate(authenticationToken);

            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
            return ResponseEntity.ok(tokenJWT);

        } catch (BadCredentialsException e) {
            // Erro 401: A senha está errada
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro: Senha incorreta.");
        } catch (InternalAuthenticationServiceException e) {
            // Erro 404: O e-mail não existe (o Spring lança isso se o UserDetailsService não achar o usuário)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Usuário não encontrado.");
        } catch (Exception e) {
            // Erro 500: Qualquer outro problema técnico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }
}
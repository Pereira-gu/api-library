package com.gu_pereira.api_libary.controllers;

import com.gu_pereira.api_libary.dto.UsuarioRequestDTO;
import com.gu_pereira.api_libary.dto.UsuarioResponseDTO;
import com.gu_pereira.api_libary.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
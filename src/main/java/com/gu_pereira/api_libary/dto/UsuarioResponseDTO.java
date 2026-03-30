package com.gu_pereira.api_libary.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime dataCriacao,
        int livrosEmprestados
) {}
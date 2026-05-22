package com.gu_pereira.api_libary.usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime dataCriacao,
        int livrosEmprestados
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getDataCriacao(), usuario.getLivrosEmprestados());
    }
}
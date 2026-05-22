package com.gu_pereira.api_libary.livro;

public record LivroResponseDTO(Long id, String titulo, String autor, String status) {
    public LivroResponseDTO(Livro livro) {
        this(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getStatus().toString());
    }
}
        
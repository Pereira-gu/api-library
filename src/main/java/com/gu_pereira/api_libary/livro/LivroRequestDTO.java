package com.gu_pereira.api_libary.livro;

import jakarta.validation.constraints.NotBlank;

public record LivroRequestDTO(
        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotBlank(message = "Autor é obrigatório")
        String autor,

        @NotBlank(message = "ISBN é obrigatório")
        String isbn,

        String categoria
) {}
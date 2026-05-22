package com.gu_pereira.api_libary.emprestimo;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequestDTO(
    @NotNull(message = "ID do usuário é obrigatório")
    Long usuarioId,
    
    @NotNull(message = "ID do livro é obrigatório")
    Long livroId
) {}
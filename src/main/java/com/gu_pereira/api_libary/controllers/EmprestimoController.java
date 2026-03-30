package com.gu_pereira.api_libary.controllers;

import com.gu_pereira.api_libary.services.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping("/{usuarioId}/{livroId}")
    public ResponseEntity<String> realizarEmprestimo(
            @PathVariable Long usuarioId,
            @PathVariable Long livroId) {

        emprestimoService.realizarEmprestimo(usuarioId, livroId);
        return ResponseEntity.ok("Empréstimo realizado com sucesso!");
    }

    @PutMapping("/devolver/{emprestimoId}")
    public ResponseEntity<String> devolverLivro(@PathVariable Long emprestimoId) {
        emprestimoService.devolverLivro(emprestimoId);
        return ResponseEntity.ok("Livro devolvido com sucesso!");
    }
}
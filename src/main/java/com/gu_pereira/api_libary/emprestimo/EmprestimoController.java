package com.gu_pereira.api_libary.emprestimo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<String> realizarEmprestimo(@RequestBody @Valid EmprestimoRequestDTO dto) {
        emprestimoService.realizarEmprestimo(dto.usuarioId(), dto.livroId());
        return ResponseEntity.ok("Empréstimo realizado com sucesso!");
    }

    @PostMapping("/{emprestimoId}/devolver")
    public ResponseEntity<String> devolverLivro(@PathVariable Long emprestimoId) {
        emprestimoService.devolverLivro(emprestimoId);
        return ResponseEntity.ok("Livro devolvido com sucesso!");
    }
}
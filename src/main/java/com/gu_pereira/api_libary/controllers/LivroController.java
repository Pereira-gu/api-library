package com.gu_pereira.api_libary.controllers;

import com.gu_pereira.api_libary.model.Livro;
import com.gu_pereira.api_libary.services.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.gu_pereira.api_libary.dto.LivroRequestDTO;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    public ResponseEntity<Livro> salvar(@RequestBody @Valid LivroRequestDTO dto) {
        // Converte DTO para Entity antes de enviar ao Service
        Livro livro = new Livro();
        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setCategoria(dto.categoria());

        return ResponseEntity.ok(livroService.salvar(livro));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Livro>> listarDisponiveis() {
        return ResponseEntity.ok(livroService.listarDisponiveis());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        livroService.desativarLivro(id);
        return ResponseEntity.noContent().build();
    }
}
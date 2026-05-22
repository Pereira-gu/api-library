package com.gu_pereira.api_libary.livro;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "livros")
@Data
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Enumerated(EnumType.STRING)
    private StatusLivro status = StatusLivro.DISPONIVEL;

    private String categoria;

    public void validarSePodeSerEmprestado() {
        if (status != StatusLivro.DISPONIVEL) {
            throw new NegocioException("Livro indisponível!");
        }
    }

    public void emprestar() {
        validarSePodeSerEmprestado();
        this.status = StatusLivro.EMPRESTADO;
    }

    public void devolver() {
        this.status = StatusLivro.DISPONIVEL;
    }
}
package com.gu_pereira.api_libary.livro;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "livros")
@Data
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE livros SET status = 'OFFLINE' WHERE id = ?")
@SQLRestriction("status <> 'OFFLINE'")
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

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
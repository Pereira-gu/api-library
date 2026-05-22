package com.gu_pereira.api_libary.livro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByCategoria(String categoria);
    List<Livro> findByStatus(StatusLivro status);
}
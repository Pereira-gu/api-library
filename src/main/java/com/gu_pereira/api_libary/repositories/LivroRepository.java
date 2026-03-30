package com.gu_pereira.api_libary.repositories;

import com.gu_pereira.api_libary.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByCategoria(String categoria);
}
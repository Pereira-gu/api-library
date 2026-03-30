package com.gu_pereira.api_libary.repositories;

import com.gu_pereira.api_libary.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
}
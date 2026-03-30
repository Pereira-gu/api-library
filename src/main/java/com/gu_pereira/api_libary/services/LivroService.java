package com.gu_pereira.api_libary.services;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.model.Livro;
import com.gu_pereira.api_libary.model.StatusLivro;
import com.gu_pereira.api_libary.repositories.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository livroRepository;

    @Transactional
    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> listarDisponiveis() {
        return livroRepository.findAll().stream()
                .filter(l -> l.getStatus().equals(StatusLivro.DISPONIVEL))
                .toList();
    }

    @Transactional
    public void desativarLivro(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Livro não encontrado"));

        // Validação de segurança: não desativar se estiver alugado
        if (livro.getStatus().equals(StatusLivro.EMPRESTADO)) {
            throw new NegocioException("Não é possível desativar um livro que está emprestado!");
        }

        livro.setStatus(StatusLivro.OFFLINE);
        livroRepository.save(livro);
    }
}
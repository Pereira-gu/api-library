package com.gu_pereira.api_libary.livro;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Anotação em nível de classe
public class LivroService {
    private final LivroRepository livroRepository;

    @Transactional // Sobrescreve o readOnly = true da classe
    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> listarDisponiveis() {
        return livroRepository.findByStatus(StatusLivro.DISPONIVEL);
    }

    @Transactional // Sobrescreve o readOnly = true da classe
    public void desativarLivro(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Livro não encontrado"));

        // Validação de segurança: não desativar se estiver alugado
        if (livro.getStatus().equals(StatusLivro.EMPRESTADO)) {
            throw new NegocioException("Não é possível desativar um livro que está emprestado!");
        }

        // Usa o método delete para acionar o @SQLDelete do Hibernate
        livroRepository.delete(livro);
    }
}
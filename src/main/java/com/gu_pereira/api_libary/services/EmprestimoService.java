package com.gu_pereira.api_libary.services;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.model.Emprestimo;
import com.gu_pereira.api_libary.model.Livro;
import com.gu_pereira.api_libary.model.StatusLivro;
import com.gu_pereira.api_libary.model.Usuario;
import com.gu_pereira.api_libary.repositories.EmprestimoRepository;
import com.gu_pereira.api_libary.repositories.LivroRepository;
import com.gu_pereira.api_libary.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Transactional
    public void realizarEmprestimo(Long usuarioId, Long livroId) {
        // 1. Primeiro busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado"));

        // 2. Agora sim valida o bloqueio
        if (usuario.isBloqueado()) {
            throw new NegocioException("Usuário bloqueado! Regularize suas multas ou atrasos.");
        }

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new NegocioException("Livro não encontrado"));

        // 3. Atualiza as mensagens para usar a NegocioException
        if (usuario.getLivrosEmprestados() >= 3) {
            throw new NegocioException("Limite de 3 livros atingido!");
        }

        if (!livro.getStatus().equals(StatusLivro.DISPONIVEL)) {
            throw new RuntimeException("Livro indisponível!");
        }

        livro.setStatus(StatusLivro.EMPRESTADO);
        usuario.setLivrosEmprestados(usuario.getLivrosEmprestados() + 1);

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setDataEmprestimo(LocalDateTime.now());
        novoEmprestimo.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(7));

        emprestimoRepository.save(novoEmprestimo);


    }
    @Transactional
    public void devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new NegocioException("Empréstimo não encontrado"));

        Livro livro = emprestimo.getLivro();
        Usuario usuario = emprestimo.getUsuario();

        // 1. Registro da data real
        LocalDateTime dataHoje = LocalDateTime.now();
        emprestimo.setDataDevolucaoReal(dataHoje);

        // 2. Cálculo de Multa (Se a data real for após a prevista)
        if (dataHoje.isAfter(emprestimo.getDataDevolucaoPrevista())) {
            long diasAtraso = java.time.Duration.between(emprestimo.getDataDevolucaoPrevista(), dataHoje).toDays();
            if (diasAtraso > 0) {
                double multaCalculada = diasAtraso * 2.0; // R$ 2,00 por dia
                emprestimo.setValorMulta(multaCalculada);
                usuario.setSaldoDevedor(usuario.getSaldoDevedor() + multaCalculada);
                usuario.setBloqueado(true); // Bloqueia automaticamente por atraso
            }
        }

        // 3. Atualização de status e contadores
        livro.setStatus(StatusLivro.DISPONIVEL);
        if (usuario.getLivrosEmprestados() > 0) {
            usuario.setLivrosEmprestados(usuario.getLivrosEmprestados() - 1);
        }
    }
}
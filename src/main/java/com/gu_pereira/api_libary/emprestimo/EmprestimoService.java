package com.gu_pereira.api_libary.emprestimo;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.livro.Livro;
import com.gu_pereira.api_libary.livro.LivroRepository;
import com.gu_pereira.api_libary.usuario.Usuario;
import com.gu_pereira.api_libary.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Value("${biblioteca.emprestimo.limite-livros:3}")
    private int limiteLivros;

    @Value("${biblioteca.emprestimo.prazo-dias:7}")
    private int prazoDias;

    @Value("${biblioteca.emprestimo.multa-diaria:2.00}")
    private BigDecimal multaDiaria;

    @Transactional
    public void realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado"));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new NegocioException("Livro não encontrado"));

        usuario.validarSePodeEmprestar(limiteLivros);
        livro.emprestar();
        usuario.registrarEmprestimo();

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setDataEmprestimo(LocalDateTime.now());
        novoEmprestimo.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(prazoDias));

        emprestimoRepository.save(novoEmprestimo);
    }

    @Transactional
    public void devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new NegocioException("Empréstimo não encontrado"));

        emprestimo.setDataDevolucaoReal(LocalDateTime.now());
        
        Livro livro = emprestimo.getLivro();
        Usuario usuario = emprestimo.getUsuario();

        livro.devolver();
        usuario.registrarDevolucao();

        long diasAtraso = Duration.between(emprestimo.getDataDevolucaoPrevista(), emprestimo.getDataDevolucaoReal()).toDays();
        if (diasAtraso > 0) {
            BigDecimal multaCalculada = multaDiaria.multiply(BigDecimal.valueOf(diasAtraso));
            emprestimo.setValorMulta(multaCalculada);
            usuario.aplicarMulta(multaCalculada);
        }
    }
}
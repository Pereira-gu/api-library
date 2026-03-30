package com.gu_pereira.api_libary.services;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.model.Livro;
import com.gu_pereira.api_libary.model.Usuario;
import com.gu_pereira.api_libary.repositories.EmprestimoRepository;
import com.gu_pereira.api_libary.repositories.LivroRepository;
import com.gu_pereira.api_libary.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private LivroRepository livroRepository;
    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    @DisplayName("Deve lançar exceção quando usuário já possui 3 livros")
    void realizarEmprestimoCenario1() {
        // Arrange (Configuração)
        Long usuarioId = 1L;
        Long livroId = 1L;

        Usuario usuarioComLimiteExcedido = new Usuario();
        usuarioComLimiteExcedido.setLivrosEmprestados(3);

        Livro livroFake = new Livro(); // Criamos um livro fake para o mock encontrar

        // Configuramos os dois repositórios
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioComLimiteExcedido));
        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livroFake)); // ADICIONE ESTA LINHA

        // Act & Assert (Ação e Validação)
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, livroId);
        });

        assertEquals("Limite de 3 livros atingido!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário estiver bloqueado por multas")
    void realizarEmprestimoCenario2() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setBloqueado(true);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioBloqueado));

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, 2L);
        });

        assertEquals("Usuário bloqueado! Regularize suas multas ou atrasos.", exception.getMessage());
    }
}
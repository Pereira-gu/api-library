package com.gu_pereira.api_libary.emprestimo;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.livro.Livro;
import com.gu_pereira.api_libary.livro.LivroRepository;
import com.gu_pereira.api_libary.livro.StatusLivro;
import com.gu_pereira.api_libary.usuario.Usuario;
import com.gu_pereira.api_libary.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(emprestimoService, "limiteLivros", 3);
        ReflectionTestUtils.setField(emprestimoService, "prazoDias", 7);
        ReflectionTestUtils.setField(emprestimoService, "multaDiaria", new BigDecimal("2.00"));
    }

    @Test
    @DisplayName("Deve realizar um empréstimo com sucesso")
    void realizarEmprestimo_CenarioSucesso() {
        // Arrange
        Long usuarioId = 1L;
        Long livroId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setBloqueado(false);
        usuario.setLivrosEmprestados(0);

        Livro livro = new Livro();
        livro.setId(livroId);
        livro.setStatus(StatusLivro.DISPONIVEL);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));

        // Act
        emprestimoService.realizarEmprestimo(usuarioId, livroId);

        // Assert
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
        assertEquals(StatusLivro.EMPRESTADO, livro.getStatus());
        assertEquals(1, usuario.getLivrosEmprestados());
    }

    @Test
    @DisplayName("Deve falhar ao tentar realizar empréstimo para um usuário bloqueado")
    void realizarEmprestimo_Falha_UsuarioBloqueado() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setId(usuarioId);
        usuarioBloqueado.setBloqueado(true);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioBloqueado));

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, 1L);
        });

        assertEquals("Usuário bloqueado! Regularize suas multas ou atrasos.", exception.getMessage());
        verify(emprestimoRepository, never()).save(any());
        verify(livroRepository, never()).findById(any()); // Garante que a busca do livro nunca ocorreu
    }

    @Test
    @DisplayName("Deve falhar ao tentar realizar empréstimo de um livro indisponível")
    void realizarEmprestimo_Falha_LivroIndisponivel() {
        // Arrange
        Long usuarioId = 1L;
        Long livroId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setBloqueado(false);

        Livro livroEmprestado = new Livro();
        livroEmprestado.setId(livroId);
        livroEmprestado.setStatus(StatusLivro.EMPRESTADO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livroEmprestado));

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, livroId);
        });

        assertEquals("Livro indisponível!", exception.getMessage());
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao tentar realizar empréstimo quando o usuário atingiu o limite")
    void realizarEmprestimo_Falha_LimiteAtingido() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setBloqueado(false);
        usuario.setLivrosEmprestados(3);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, 1L);
        });

        assertEquals("Limite de 3 livros atingido!", exception.getMessage());
        verify(emprestimoRepository, never()).save(any());
        verify(livroRepository, never()).findById(any()); // Garante que a busca do livro nunca ocorreu
    }
}

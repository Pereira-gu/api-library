package com.gu_pereira.api_libary.emprestimo;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.livro.Livro;
import com.gu_pereira.api_libary.livro.LivroRepository;
import com.gu_pereira.api_libary.usuario.Usuario;
import com.gu_pereira.api_libary.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("Deve falhar ao tentar realizar empréstimo para um usuário bloqueado")
    void realizarEmprestimo_Cenario1_FalhaUsuarioBloqueado() {
        // Arrange (Arrumar o cenário)
        Long usuarioId = 1L;
        Long livroId = 1L;

        // 1. Crie um usuário "dublê" que está bloqueado
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setId(usuarioId);
        usuarioBloqueado.setBloqueado(true);

        // 2. Ensine o repositório "dublê" a retornar este usuário quando for chamado
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioBloqueado));

        // Act & Assert (Agir e Afirmar)
        // 3. Verifique se a exceção NegocioException é lançada ao chamar o método
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            emprestimoService.realizarEmprestimo(usuarioId, livroId);
        });

        // 4. Verifique se a mensagem da exceção é a que esperamos
        assertEquals("Usuário bloqueado! Regularize suas multas ou atrasos.", exception.getMessage());

        // 5. (Opcional, mas boa prática) Verifique se o método save nunca foi chamado em nenhum repositório
        verify(emprestimoRepository, never()).save(any());
    }
}

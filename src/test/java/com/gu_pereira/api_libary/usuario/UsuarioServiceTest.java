package com.gu_pereira.api_libary.usuario;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // --- Testes para o método cadastrar ---

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void cadastrar_CenarioSucesso() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome Teste", "teste@email.com", "12345678901", "senha123");
        when(usuarioRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senha_criptografada");

        // ** A CORREÇÃO ESTÁ AQUI **
        // Ensina o mock a retornar o mesmo usuário que ele recebe no save
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioPassado = invocation.getArgument(0);
            usuarioPassado.setId(1L); // Simula o banco de dados gerando um ID
            return usuarioPassado;
        });

        // Act
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Nome Teste", usuarioSalvo.getNome());
        assertEquals("senha_criptografada", usuarioSalvo.getSenha());
        assertEquals(Role.ROLE_USER, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar usuário com CPF duplicado")
    void cadastrar_Falha_CpfDuplicado() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome Teste", "teste@email.com", "12345678901", "senha123");
        when(usuarioRepository.existsByCpf(dto.cpf())).thenReturn(true);

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            usuarioService.cadastrar(dto);
        });

        assertEquals("CPF já cadastrado!", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // --- Testes para o método bloquear ---

    @Test
    @DisplayName("Deve bloquear um usuário com sucesso")
    void bloquear_CenarioSucesso() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setBloqueado(false);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.bloquear(usuarioId);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertTrue(usuarioCaptor.getValue().isBloqueado());
    }

    // --- Testes para o método desbloquear ---

    @Test
    @DisplayName("Deve desbloquear um usuário e zerar a dívida")
    void desbloquear_CenarioSucesso() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setBloqueado(true);
        usuario.setSaldoDevedor(new BigDecimal("50.00"));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.desbloquear(usuarioId);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertFalse(usuarioSalvo.isBloqueado());
        assertEquals(0, usuarioSalvo.getSaldoDevedor().compareTo(BigDecimal.ZERO));
    }

    // --- Testes para o método atualizarRole ---

    @Test
    @DisplayName("Deve atualizar a role de um usuário com sucesso")
    void atualizarRole_CenarioSucesso() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setRole(Role.ROLE_USER);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.atualizarRole(usuarioId, Role.ROLE_ADMIN);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        assertEquals(Role.ROLE_ADMIN, usuarioCaptor.getValue().getRole());
    }

    // --- Testes para o método pagarMinhaMulta ---

    @Test
    @DisplayName("Deve permitir que um usuário pague sua própria multa")
    void pagarMinhaMulta_CenarioSucesso() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setBloqueado(true);
        usuario.setSaldoDevedor(new BigDecimal("20.00"));

        // Act
        usuarioService.pagarMinhaMulta(usuario);

        // Assert
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertFalse(usuarioSalvo.isBloqueado());
        assertEquals(0, usuarioSalvo.getSaldoDevedor().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Deve falhar ao tentar pagar multa quando não há dívida")
    void pagarMinhaMulta_Falha_SemDivida() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setBloqueado(false);
        usuario.setSaldoDevedor(BigDecimal.ZERO);

        // Act & Assert
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            usuarioService.pagarMinhaMulta(usuario);
        });

        assertEquals("Usuário não possui multas ou bloqueios pendentes.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}

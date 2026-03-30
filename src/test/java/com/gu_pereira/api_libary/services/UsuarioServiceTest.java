package com.gu_pereira.api_libary.services;

import com.gu_pereira.api_libary.dto.UsuarioRequestDTO;
import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void naoDeveCadastrarUsuarioComCpfDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Teste", "t@t.com", "12345678901", "123456");

        when(repository.existsByCpf(dto.cpf())).thenReturn(true);

        assertThrows(NegocioException.class, () -> service.cadastrar(dto));
    }
}
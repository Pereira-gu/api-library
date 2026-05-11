package com.gu_pereira.api_libary.services;

import com.gu_pereira.api_libary.dto.UsuarioRequestDTO;
import com.gu_pereira.api_libary.dto.UsuarioResponseDTO;
import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import com.gu_pereira.api_libary.model.Usuario;
import com.gu_pereira.api_libary.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Injeção do Bean criado acima

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new NegocioException("CPF já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCpf(dto.cpf());

        // CRIPTOGRAFIA AQUI: Nunca salve texto puro!
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                salvo.getId(), salvo.getNome(), salvo.getEmail(),
                salvo.getDataCriacao(), salvo.getLivrosEmprestados()
        );
    }

    @Transactional
    public void pagarMulta(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado"));

        usuario.setSaldoDevedor(BigDecimal.ZERO);
        usuario.setBloqueado(false); // Desbloqueia após pagamento
    }
}
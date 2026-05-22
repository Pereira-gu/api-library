package com.gu_pereira.api_libary.usuario;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setRole(Role.ROLE_USER);

        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(salvo);
    }

    @Transactional
    public void pagarMulta(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado"));

        usuario.setSaldoDevedor(BigDecimal.ZERO);
        usuario.setBloqueado(false); // Desbloqueia após pagamento
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado com o ID: " + id));
        return new UsuarioResponseDTO(usuario);
    }
}
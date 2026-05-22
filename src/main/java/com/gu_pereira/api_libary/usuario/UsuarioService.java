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
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public void bloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado com o ID: " + id));
        usuario.setBloqueado(true);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desbloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado com o ID: " + id));
        usuario.setBloqueado(false);
        usuario.setSaldoDevedor(BigDecimal.ZERO);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarRole(Long id, Role novaRole) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado com o ID: " + id));
        usuario.setRole(novaRole);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void pagarMinhaMulta(Usuario usuario) {
        if (!usuario.isBloqueado() && usuario.getSaldoDevedor().compareTo(BigDecimal.ZERO) == 0) {
            throw new NegocioException("Usuário não possui multas ou bloqueios pendentes.");
        }

        // Em um sistema real, aqui ocorreria a integração com um gateway de pagamento.
        // Como estamos simulando, simplesmente quitamos a dívida.

        usuario.setSaldoDevedor(BigDecimal.ZERO);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);
    }
}
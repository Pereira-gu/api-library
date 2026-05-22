package com.gu_pereira.api_libary.usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<Void> bloquearUsuario(@PathVariable Long id) {
        usuarioService.bloquear(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquearUsuario(@PathVariable Long id) {
        usuarioService.desbloquear(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> atualizarRole(@PathVariable Long id, @RequestBody @Valid RoleUpdateRequestDTO dto) {
        usuarioService.atualizarRole(id, dto.novaRole());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/pagar-multa")
    public ResponseEntity<Void> pagarMinhaMulta(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        usuarioService.pagarMinhaMulta(usuarioAutenticado);
        return ResponseEntity.noContent().build();
    }
}
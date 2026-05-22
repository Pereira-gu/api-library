package com.gu_pereira.api_libary.infrastructure;

import com.gu_pereira.api_libary.usuario.Role;
import com.gu_pereira.api_libary.usuario.Usuario;
import com.gu_pereira.api_libary.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // E-mail do admin que queremos garantir que exista
        String adminEmail = "admin@biblioteca.com";

        // Verifica se o usuário admin já existe no banco
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            System.out.println("Nenhum administrador encontrado, criando um novo...");

            Usuario admin = new Usuario();
            admin.setNome("Admin");
            admin.setEmail(adminEmail);
            admin.setCpf("00000000000"); // CPF genérico para o admin
            admin.setSenha(passwordEncoder.encode("admin123")); // Senha padrão
            admin.setRole(Role.ROLE_ADMIN);

            usuarioRepository.save(admin);
            System.out.println("Administrador criado com sucesso!");
        } else {
            System.out.println("Administrador já existe.");
        }
    }
}
package com.gu_pereira.api_libary.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario implements UserDetails { // Implementa UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String senha;

    private LocalDateTime dataCriacao;

    //controle de limite

    private int livrosEmprestados = 0;

    @Transient
    private final int limiteMaximo = 3;

    @PrePersist
    protected void onCreate(){
        dataCriacao = LocalDateTime.now();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por padrão, todos serão USER. Em um sistema real, você teria um campo 'role' no banco.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha; // Retorna a senha criptografada
    }

    @Override
    public String getUsername() {
        return email; // O email será o nosso "username" para login
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    private boolean bloqueado = false;
    private double saldoDevedor = 0.0;
}
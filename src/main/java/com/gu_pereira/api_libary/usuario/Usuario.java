package com.gu_pereira.api_libary.usuario;

import com.gu_pereira.api_libary.infrastructure.exceptions.NegocioException;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime dataCriacao;

    private int livrosEmprestados = 0;

    private boolean bloqueado = false;
    
    private BigDecimal saldoDevedor = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate(){
        dataCriacao = LocalDateTime.now();
    }

    public void validarSePodeEmprestar(int limiteLivros) {
        if (isBloqueado()) {
            throw new NegocioException("Usuário bloqueado! Regularize suas multas ou atrasos.");
        }
        if (getLivrosEmprestados() >= limiteLivros) {
            throw new NegocioException("Limite de " + limiteLivros + " livros atingido!");
        }
    }

    public void registrarEmprestimo() {
        this.livrosEmprestados++;
    }

    public void registrarDevolucao() {
        if (this.livrosEmprestados > 0) {
            this.livrosEmprestados--;
        }
    }

    public void aplicarMulta(BigDecimal valorMulta) {
        this.saldoDevedor = this.saldoDevedor.add(valorMulta);
        this.bloqueado = true;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !bloqueado; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
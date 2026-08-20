package com.empresa.cadrastro_pessoas.auth.security;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public record UsuarioPrincipal(
        Long id,
        String email,
        String senhaHash,
        Perfil perfil,
        boolean ativo,
        Long pessoaId,
        String pessoaNome
) implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static UsuarioPrincipal de(Usuario usuario) {
        Long pessoaId = usuario.getPessoa() == null ? null : usuario.getPessoa().getId();
        String pessoaNome = usuario.getPessoa() == null ? null : usuario.getPessoa().getNome();

        return new UsuarioPrincipal(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPerfil(),
                Boolean.TRUE.equals(usuario.getAtivo()),
                pessoaId,
                pessoaNome
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}

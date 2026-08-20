package com.empresa.cadrastro_pessoas.usuario.dto;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String email,
        Perfil perfil,
        boolean ativo,
        Long pessoaId,
        String pessoaNome,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPerfil(),
                Boolean.TRUE.equals(usuario.getAtivo()),
                usuario.getPessoa() == null ? null : usuario.getPessoa().getId(),
                usuario.getPessoa() == null ? null : usuario.getPessoa().getNome(),
                usuario.getCriadoEm()
        );
    }
}

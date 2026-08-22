package com.empresa.cadrastro_pessoas.usuario.dto;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String email,
        Perfil perfil,
        boolean ativo,
        boolean acessoLiberado,
        Long pessoaId,
        String pessoaNome,
        Boolean pessoaAtiva,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse de(Usuario usuario) {
        boolean pessoaAtiva = usuario.getPessoa() == null
                || Boolean.TRUE.equals(usuario.getPessoa().getAtivo());
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPerfil(),
                Boolean.TRUE.equals(usuario.getAtivo()),
                Boolean.TRUE.equals(usuario.getAtivo()) && pessoaAtiva,
                usuario.getPessoa() == null ? null : usuario.getPessoa().getId(),
                usuario.getPessoa() == null ? null : usuario.getPessoa().getNome(),
                usuario.getPessoa() == null ? null : pessoaAtiva,
                usuario.getCriadoEm()
        );
    }
}

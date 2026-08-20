package com.empresa.cadrastro_pessoas.auth.dto;

import com.empresa.cadrastro_pessoas.auth.security.UsuarioPrincipal;
import com.empresa.cadrastro_pessoas.usuario.Perfil;

public record SessaoResponse(
        Long usuarioId,
        String email,
        Perfil perfil,
        Long pessoaId,
        String pessoaNome
) {
    public static SessaoResponse de(UsuarioPrincipal principal) {
        return new SessaoResponse(
                principal.id(),
                principal.email(),
                principal.perfil(),
                principal.pessoaId(),
                principal.pessoaNome()
        );
    }
}

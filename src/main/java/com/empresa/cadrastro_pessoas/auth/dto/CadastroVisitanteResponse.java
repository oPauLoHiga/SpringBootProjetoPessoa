package com.empresa.cadrastro_pessoas.auth.dto;

import com.empresa.cadrastro_pessoas.usuario.Perfil;

public record CadastroVisitanteResponse(
        Long usuarioId,
        Long pessoaId,
        String nome,
        String email,
        Perfil perfil
) {
}

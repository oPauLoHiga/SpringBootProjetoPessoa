package com.empresa.cadrastro_pessoas.usuario.dto;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import jakarta.validation.constraints.NotNull;

public record AlterarPerfilRequest(
        @NotNull(message = "Perfil é obrigatório") Perfil perfil
) {
}

package com.empresa.cadrastro_pessoas.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
        String senha,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        @Size(max = 72, message = "Confirmação de senha deve ter no máximo 72 caracteres")
        String confirmacaoSenha
) {
}

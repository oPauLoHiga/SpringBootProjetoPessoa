package com.empresa.cadrastro_pessoas.usuario.dto;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarUsuarioRequest {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
    private String senha;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    @Size(min = 8, max = 72, message = "Confirmação de senha deve ter entre 8 e 72 caracteres")
    private String confirmacaoSenha;

    @NotNull(message = "Perfil é obrigatório")
    private Perfil perfil;
}

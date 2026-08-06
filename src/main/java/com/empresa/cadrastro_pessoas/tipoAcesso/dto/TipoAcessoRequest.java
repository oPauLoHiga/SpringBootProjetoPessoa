package com.empresa.cadrastro_pessoas.tipoacesso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoAcessoRequest {

    @NotBlank(message = "Nome do tipo de acesso é obrigatorio")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    @Size(max = 200, message = "Descricao deve ter no maximo 200 caracteres")
    private String descricao;

}

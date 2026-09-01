package com.empresa.cadrastro_pessoas.sugestao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SugestaoRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, max = 100, message = "Título deve ter entre 5 e 100 caracteres")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricao;

    @NotNull(message = "ID da pessoa é obrigatório")
    @Positive(message = "ID da pessoa deve ser positivo")
    private Long pessoaId;

}

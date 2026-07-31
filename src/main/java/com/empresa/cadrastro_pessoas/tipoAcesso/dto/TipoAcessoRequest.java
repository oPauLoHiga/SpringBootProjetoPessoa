package com.empresa.cadrastro_pessoas.tipoAcesso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoAcessoRequest {

    @NotBlank(message = "Nome do tipo de acesso é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
}

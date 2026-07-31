package com.empresa.cadrastro_pessoas.tipoAcesso.dto;

import com.empresa.cadrastro_pessoas.tipoAcesso.TipoAcesso;

public class TipoAcessoResponse {

    private Long    id;
    private String  nome;
    private String  descricao;
    private boolean ativo;
    private int     totalPessoas; // quantas pessoas têm este tipo

    // Construtor estático que converte entidade → DTO
    public static TipoAcessoResponse de(TipoAcesso t) {
        TipoAcessoResponse r = new TipoAcessoResponse();
        r.id           = t.getId();
        r.nome         = t.getNome();
        r.descricao    = t.getDescricao();
        r.ativo        = t.isAtivo();
        r.totalPessoas = t.getPessoas() != null ? t.getPessoas().size() : 0;
        return r;
    }

    // Getters
    public Long    getId()          { return id; }
    public String  getNome()        { return nome; }
    public String  getDescricao()   { return descricao; }
    public boolean isAtivo()        { return ativo; }
    public int     getTotalPessoas(){ return totalPessoas; }
}

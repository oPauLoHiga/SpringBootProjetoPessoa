package com.empresa.cadrastro_pessoas.tipoacesso.dto;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import lombok.Getter;

@Getter
public class TipoAcessoResponse {

    private Long    id;
    private String  nome;
    private String  descricao;
    private boolean ativo;
    private long    totalPessoas;

    public TipoAcessoResponse(
            Long id,
            String nome,
            String descricao,
            boolean ativo,
            long totalPessoas
    ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
        this.totalPessoas = totalPessoas;
    }

    private TipoAcessoResponse() {
    }

    public static TipoAcessoResponse de(TipoAcesso t) {
        TipoAcessoResponse r = new TipoAcessoResponse();
        r.id           = t.getId();
        r.nome         = t.getNome();
        r.descricao    = t.getDescricao();
        r.ativo        = t.isAtivo();
        r.totalPessoas = t.getPessoas() != null ? t.getPessoas().size() : 0;
        return r;
    }

}

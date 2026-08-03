package com.empresa.cadrastro_pessoas.sugestao.dto;

import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import com.empresa.cadrastro_pessoas.sugestao.model.Sugestao;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SugestaoResponse {

    private Long           id;
    private String         titulo;
    private String         descricao;
    private StatusSugestao status;
    private LocalDateTime dataCriacao;

    // Dados básicos da pessoa autora (evita retornar a entidade Pessoa inteira)
    private Long   pessoaId;
    private String pessoaNome;

    public static SugestaoResponse de(Sugestao s) {
        SugestaoResponse r = new SugestaoResponse();
        r.id          = s.getId();
        r.titulo      = s.getTitulo();
        r.descricao   = s.getDescricao();
        r.status      = s.getStatus();
        r.dataCriacao = s.getDataCriacao();
        r.pessoaId    = s.getPessoa().getId();
        r.pessoaNome  = s.getPessoa().getNome();
        return r;
    }

}

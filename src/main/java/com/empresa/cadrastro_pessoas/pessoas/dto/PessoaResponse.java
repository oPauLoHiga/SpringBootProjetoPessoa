package com.empresa.cadrastro_pessoas.pessoas.dto;

import com.empresa.cadrastro_pessoas.pessoas.model.Pessoa;
import lombok.Getter;

@Getter
public class PessoaResponse {
    private Long tipoAcessoId;
    private String tipoAcessoNome;

    public static PessoaResponse de(Pessoa p) {
        PessoaResponse r = new PessoaResponse();

        r.tipoAcessoId = p.getTipoAcesso() != null
                ? p.getTipoAcesso().getId()
                : null;

        r.tipoAcessoNome = p.getTipoAcesso() != null
                ? p.getTipoAcesso().getNome()
                : null;

        return r;
    }

}
package com.empresa.cadrastro_pessoas.tipoacesso.dto;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import lombok.Getter;

import java.util.Locale;

@Getter
public class TipoAcessoResponse {

    private Long    id;
    private String  nome;
    private String  descricao;
    private boolean ativo;
    private long    totalPessoas;
    private Perfil  perfil;

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
        this.perfil = identificarPerfil(nome);
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
        r.perfil       = identificarPerfil(t.getNome());
        return r;
    }

    private static Perfil identificarPerfil(String nome) {
        if (nome == null) {
            return null;
        }
        try {
            return Perfil.valueOf(nome.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}

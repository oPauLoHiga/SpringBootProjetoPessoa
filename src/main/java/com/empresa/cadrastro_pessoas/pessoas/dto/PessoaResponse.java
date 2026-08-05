package com.empresa.cadrastro_pessoas.pessoas.dto;

import com.empresa.cadrastro_pessoas.pessoas.model.Pessoa;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PessoaResponse {
    private Long id;
    private String cpf;
    private LocalDate dataNascimento;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private Boolean ativo;

    private Long tipoAcessoId;
    private String tipoAcessoNome;

    public static PessoaResponse de(Pessoa p) {
        PessoaResponse r = new PessoaResponse();

            r.id = p.getId();
            r.cpf = p.getCpf();
            r.nome = p.getNome();
            r.dataNascimento = p.getDataNascimento();
            r.email = p.getEmail();
            r.telefone = p.getTelefone();
            r.cidade = p.getCidade();
            r.estado = p.getEstado();
            r.ativo = p.getAtivo();

        r.tipoAcessoId = p.getTipoAcesso() != null
                ? p.getTipoAcesso().getId()
                : null;

        r.tipoAcessoNome = p.getTipoAcesso() != null
                ? p.getTipoAcesso().getNome()
                : null;

        return r;
    }

}
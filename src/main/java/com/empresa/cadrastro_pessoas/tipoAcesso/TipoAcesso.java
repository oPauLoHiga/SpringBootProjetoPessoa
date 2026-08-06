package com.empresa.cadrastro_pessoas.tipoacesso;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tipo_acesso")
public class TipoAcesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;


    @OneToMany(mappedBy = "tipoAcesso", fetch = FetchType.LAZY)
    private List<Pessoa> pessoas;

    // Construtores
    public TipoAcesso() {

    }

    public TipoAcesso(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Pessoa> getPessoas() { return pessoas; }
    public void setPessoas(List<Pessoa> pessoas) { this.pessoas = pessoas; }

}

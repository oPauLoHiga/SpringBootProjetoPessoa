package com.empresa.cadrastro_pessoas.sugestao.model;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sugestao")
public class Sugestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    // TEXT no banco â€” suporta textos longos
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    // EnumType.STRING salva "PENDENTE", "APROVADA" etc. no banco
    // Nunca use EnumType.ORDINAL (quebraria ao reordenar o enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSugestao status = StatusSugestao.PENDENTE;

    // updatable = false: data Ã© definida na criaÃ§Ã£o e nunca alterada
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // @ManyToOne: muitas sugestÃµes para uma pessoa
    // optional = false: sugestÃ£o DEVE ter uma pessoa vinculada
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    // @PrePersist: executado pelo JPA ANTES de inserir no banco
    // Garante que dataCriacao seja sempre preenchida automaticamente
    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtores
    public Sugestao() {}

    // Getters e Setters
    public Long          getId()          { return id; }
    public void          setId(Long id)  { this.id = id; }

    public String         getTitulo()      { return titulo; }
    public void          setTitulo(String titulo) { this.titulo = titulo; }

    public String         getDescricao()   { return descricao; }
    public void          setDescricao(String descricao) { this.descricao = descricao; }

    public StatusSugestao getStatus()      { return status; }
    public void          setStatus(StatusSugestao status) { this.status = status; }

    public LocalDateTime  getDataCriacao() { return dataCriacao; }

    public Pessoa         getPessoa()      { return pessoa; }
    public void          setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }
}

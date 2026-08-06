package com.empresa.cadrastro_pessoas.pessoa.model;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pessoas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome Ã© obrigatÃ³rio")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 a 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "CPF Ã© obrigatÃ³rio")
    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @NotBlank(message = "E-mail Ã© obrigatÃ³rio")
    @Email(message = "E-mail invÃ¡lido")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @NotNull(message = "Data de nascimento Ã© obrigatÃ³ria")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "criado_em")
    private LocalDate criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.from(LocalDateTime.now());
        this.atualizadoEm = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tipo_acesso_id")
    private TipoAcesso tipoAcesso;

    // Getter e Setter correspondentes:
    public TipoAcesso getTipoAcesso() { return tipoAcesso; }
    public void setTipoAcesso(TipoAcesso tipoAcesso) { this.tipoAcesso = tipoAcesso; }
}

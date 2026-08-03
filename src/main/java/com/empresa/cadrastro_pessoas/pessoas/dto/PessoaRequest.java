package com.empresa.cadrastro_pessoas.pessoas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaRequest {

    @NotBlank
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatorio")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 000.000.000-00")
    private String cpf;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    private String endereco;
    private String cidade;

    @Size(max = 2, message = "Estado deve ter 2 caracteres (sigla)")
    private String estado;

    // Adicione em PessoaRequest.java (campo opcional — não é obrigatório):
    private Long tipoAcessoId; // ID do tipo de acesso (pode ser null)

    // Getter e Setter:
    public Long getTipoAcessoId()            { return tipoAcessoId; }
    public void setTipoAcessoId(Long id)    { this.tipoAcessoId = id; }
}

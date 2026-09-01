package com.empresa.cadrastro_pessoas.pessoa.dto;

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

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 000.000.000-00")
    private String cpf;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String endereco;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Pattern(regexp = "^$|[A-Za-z]{2}", message = "Estado deve conter uma sigla de 2 letras")
    private String estado;

    @Positive(message = "ID do tipo de acesso deve ser positivo")
    private Long tipoAcessoId;
}

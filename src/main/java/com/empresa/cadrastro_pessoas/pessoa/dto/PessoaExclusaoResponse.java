package com.empresa.cadrastro_pessoas.pessoa.dto;

public record PessoaExclusaoResponse(
        Long id,
        String nome,
        long totalSugestoes
) {
}

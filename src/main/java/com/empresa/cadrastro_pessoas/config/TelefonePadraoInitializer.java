package com.empresa.cadrastro_pessoas.config;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TelefonePadraoInitializer implements CommandLineRunner {
    private final PessoaRepository pessoaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        for (Pessoa pessoa : pessoaRepository.findAll()) {
            String telefone = pessoa.getTelefone();
            if (telefone == null || telefone.isBlank()) {
                continue;
            }

            String numeros = telefone.replaceAll("\\D", "");
            if (numeros.length() != 10 && numeros.length() != 11) {
                numeros = String.format("119%08d", pessoa.getId());
            }
            pessoa.setTelefone(numeros);
        }
    }
}

package com.empresa.cadrastro_pessoas.config;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TipoAcessoPadraoInitializer implements CommandLineRunner {
    private final TipoAcessoRepository tipoAcessoRepository;
    private final PessoaRepository pessoaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        TipoAcesso visitante = tipoAcessoRepository.findByNomeIgnoreCase("Visitante")
                .orElseGet(() -> tipoAcessoRepository.save(
                        new TipoAcesso("Visitante", "Acesso padrÃ£o com permissÃµes limitadas.")));

        visitante.setAtivo(true);
        for (Pessoa pessoa : pessoaRepository.findByTipoAcessoIsNull()) {
            pessoa.setTipoAcesso(visitante);
        }
    }
}

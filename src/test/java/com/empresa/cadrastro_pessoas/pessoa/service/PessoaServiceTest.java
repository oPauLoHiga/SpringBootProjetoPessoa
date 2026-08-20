package com.empresa.cadrastro_pessoas.pessoa.service;

import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private TipoAcessoRepository tipoAcessoRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremPessoas() {
        when(pessoaRepository.findAll()).thenReturn(List.of());

        assertThat(pessoaService.listarTodas()).isEmpty();
    }
}

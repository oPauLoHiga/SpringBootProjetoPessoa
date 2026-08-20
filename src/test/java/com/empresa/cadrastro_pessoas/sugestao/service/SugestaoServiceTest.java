package com.empresa.cadrastro_pessoas.sugestao.service;

import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SugestaoServiceTest {

    @Test
    void deveInformarQuandoSugestaoNaoExistir() {
        SugestaoRepository sugestaoRepository = mock(SugestaoRepository.class);
        PessoaRepository pessoaRepository = mock(PessoaRepository.class);
        SugestaoService service = new SugestaoService(sugestaoRepository, pessoaRepository);
        when(sugestaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Sugestão não encontrada: 99");
    }
}

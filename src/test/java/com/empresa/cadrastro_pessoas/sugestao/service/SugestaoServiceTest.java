package com.empresa.cadrastro_pessoas.sugestao.service;

import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Test
    void naoDeveAceitarTituloCurtoDisfarcadoComEspacos() {
        SugestaoRepository sugestaoRepository = mock(SugestaoRepository.class);
        PessoaRepository pessoaRepository = mock(PessoaRepository.class);
        SugestaoService service = new SugestaoService(sugestaoRepository, pessoaRepository);
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Maria").ativo(true).build();
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        assertThatThrownBy(() -> service.criarParaPessoa(1L, "  abc  ", "Descrição válida"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Título deve ter entre 5 e 100 caracteres.");

        verify(sugestaoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void naoDeveAceitarDescricaoCurtaDisfarcadaComEspacos() {
        SugestaoRepository sugestaoRepository = mock(SugestaoRepository.class);
        PessoaRepository pessoaRepository = mock(PessoaRepository.class);
        SugestaoService service = new SugestaoService(sugestaoRepository, pessoaRepository);
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Maria").ativo(true).build();
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        assertThatThrownBy(() -> service.criarParaPessoa(1L, "Título válido", "   curto   "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Descrição deve ter entre 10 e 1000 caracteres.");

        verify(sugestaoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

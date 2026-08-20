package com.empresa.cadrastro_pessoas.pessoa.service;

import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaExclusaoResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private TipoAcessoRepository tipoAcessoRepository;

    @Mock
    private SugestaoRepository sugestaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremPessoas() {
        when(pessoaRepository.findAll()).thenReturn(List.of());

        assertThat(pessoaService.listarTodas()).isEmpty();
    }

    @Test
    void deveInformarQuantasSugestoesSeraoExcluidas() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Maria").build();
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(sugestaoRepository.countByPessoaId(1L)).thenReturn(2L);

        PessoaExclusaoResponse resumo = pessoaService.obterResumoExclusao(1L);

        assertThat(resumo.id()).isEqualTo(1L);
        assertThat(resumo.nome()).isEqualTo("Maria");
        assertThat(resumo.totalSugestoes()).isEqualTo(2L);
        assertThat(resumo.contaVinculada()).isFalse();
        verifyNoInteractions(tipoAcessoRepository);
    }

    @Test
    void deveExcluirSugestoesAntesDeExcluirPessoa() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Maria").build();
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(sugestaoRepository.countByPessoaId(1L)).thenReturn(2L);
        when(usuarioRepository.existsByPessoaId(1L)).thenReturn(true);

        PessoaExclusaoResponse resultado = pessoaService.excluir(1L);

        assertThat(resultado.totalSugestoes()).isEqualTo(2L);
        assertThat(resultado.contaVinculada()).isTrue();

        var ordem = inOrder(usuarioRepository, sugestaoRepository, pessoaRepository);
        ordem.verify(usuarioRepository).deleteByPessoaId(1L);
        ordem.verify(sugestaoRepository).deleteByPessoaId(1L);
        ordem.verify(pessoaRepository).delete(pessoa);
    }
}

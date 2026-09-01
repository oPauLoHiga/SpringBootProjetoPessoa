package com.empresa.cadrastro_pessoas.pessoa.service;

import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaExclusaoResponse;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

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
        when(pessoaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of());

        assertThat(pessoaService.listarTodas()).isEmpty();
    }

    @Test
    void naoDeveCadastrarNomeCurtoDisfarcadoComEspacos() {
        PessoaRequest request = PessoaRequest.builder()
                .nome(" A ")
                .cpf("123.456.789-00")
                .email("pessoa@exemplo.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> pessoaService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome deve ter pelo menos 2 caracteres.");

        verify(pessoaRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarPessoaComTipoDeAcessoInativo() {
        PessoaRequest request = PessoaRequest.builder()
                .nome("Maria")
                .cpf("123.456.789-00")
                .email("pessoa@exemplo.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .tipoAcessoId(2L)
                .build();
        TipoAcesso tipoInativo = new TipoAcesso("Temporário", null);
        tipoInativo.setId(2L);
        tipoInativo.setAtivo(false);
        when(tipoAcessoRepository.findById(2L)).thenReturn(Optional.of(tipoInativo));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> pessoaService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível vincular uma pessoa a um tipo de acesso inativo.");

        verify(pessoaRepository, never()).save(any());
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

        PessoaExclusaoResponse resultado = pessoaService.excluir(1L, 99L);

        assertThat(resultado.totalSugestoes()).isEqualTo(2L);
        assertThat(resultado.contaVinculada()).isTrue();

        var ordem = inOrder(usuarioRepository, sugestaoRepository, pessoaRepository);
        ordem.verify(usuarioRepository).deleteByPessoaId(1L);
        ordem.verify(sugestaoRepository).deleteByPessoaId(1L);
        ordem.verify(pessoaRepository).delete(pessoa);
    }

    @Test
    void deveAtualizarEmailDaContaVinculadaJuntoComAPessoa() {
        Pessoa pessoa = Pessoa.builder().id(1L).email("antigo@exemplo.com").build();
        Usuario usuario = Usuario.builder()
                .id(7L)
                .email("antigo@exemplo.com")
                .perfil(Perfil.VISITANTE)
                .pessoa(pessoa)
                .build();
        PessoaRequest request = PessoaRequest.builder()
                .nome("Maria Atualizada")
                .cpf("123.456.789-00")
                .email(" NOVO@EXEMPLO.COM ")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .estado("sp")
                .build();

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.findByCpf(request.getCpf())).thenReturn(Optional.empty());
        when(pessoaRepository.findByEmailIgnoreCase("novo@exemplo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmailIgnoreCase("novo@exemplo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByPessoaId(1L)).thenReturn(Optional.of(usuario));
        when(pessoaRepository.save(pessoa)).thenReturn(pessoa);

        pessoaService.atualizar(1L, request);

        assertThat(pessoa.getEmail()).isEqualTo("novo@exemplo.com");
        assertThat(pessoa.getEstado()).isEqualTo("SP");
        assertThat(usuario.getEmail()).isEqualTo("novo@exemplo.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveExcluirPessoaDaPropriaConta() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Admin").build();
        Usuario usuario = Usuario.builder().id(7L).pessoa(pessoa).build();
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(usuarioRepository.findByPessoaId(1L)).thenReturn(Optional.of(usuario));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> pessoaService.excluir(1L, 7L))
                .isInstanceOf(com.empresa.cadrastro_pessoas.shared.exception.BusinessException.class)
                .hasMessage("Você não pode excluir a pessoa vinculada à sua própria conta.");

        verify(usuarioRepository, never()).deleteByPessoaId(anyLong());
        verify(sugestaoRepository, never()).deleteByPessoaId(anyLong());
        verify(pessoaRepository, never()).delete(any());
    }
}

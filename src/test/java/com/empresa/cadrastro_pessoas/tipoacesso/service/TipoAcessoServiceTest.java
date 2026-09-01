package com.empresa.cadrastro_pessoas.tipoacesso.service;

import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TipoAcessoServiceTest {

    @Test
    void deveInformarQuandoTipoDeAcessoNaoExistir() {
        TipoAcessoRepository repository = mock(TipoAcessoRepository.class);
        TipoAcessoService service = new TipoAcessoService(repository);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de acesso não encontrado: 99");
    }

    @Test
    void naoDeveDesativarOuExcluirOTipoVisitante() {
        TipoAcessoRepository repository = mock(TipoAcessoRepository.class);
        TipoAcessoService service = new TipoAcessoService(repository);
        TipoAcesso visitante = new TipoAcesso("Visitante", "Padrão");
        visitante.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(visitante));

        assertThatThrownBy(() -> service.desativar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pode ser desativado");
        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pode ser excluído");
    }

    @Test
    void deveImpedirNomeDuplicadoAoAtualizar() {
        TipoAcessoRepository repository = mock(TipoAcessoRepository.class);
        TipoAcessoService service = new TipoAcessoService(repository);
        TipoAcesso atual = new TipoAcesso("Básico", null);
        atual.setId(1L);
        TipoAcesso existente = new TipoAcesso("Premium", null);
        existente.setId(2L);
        TipoAcessoRequest request = new TipoAcessoRequest();
        request.setNome(" Premium ");

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        when(repository.findByNomeIgnoreCase("Premium")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.atualizar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um tipo de acesso com o nome: Premium");
    }

    @Test
    void deveRemoverEspacosAoCriarTipo() {
        TipoAcessoRepository repository = mock(TipoAcessoRepository.class);
        TipoAcessoService service = new TipoAcessoService(repository);
        TipoAcessoRequest request = new TipoAcessoRequest();
        request.setNome("  Premium  ");
        request.setDescricao("  Acesso ampliado  ");
        when(repository.save(org.mockito.ArgumentMatchers.any(TipoAcesso.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        var resposta = service.criar(request);

        assertThat(resposta.getNome()).isEqualTo("Premium");
        assertThat(resposta.getDescricao()).isEqualTo("Acesso ampliado");
    }

    @Test
    void naoDeveCriarNomeCurtoDisfarcadoComEspacos() {
        TipoAcessoRepository repository = mock(TipoAcessoRepository.class);
        TipoAcessoService service = new TipoAcessoService(repository);
        TipoAcessoRequest request = new TipoAcessoRequest();
        request.setNome(" A ");

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome deve ter pelo menos 2 caracteres.");
    }
}

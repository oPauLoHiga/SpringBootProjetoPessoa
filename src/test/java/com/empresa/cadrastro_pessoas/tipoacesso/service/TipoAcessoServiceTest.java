package com.empresa.cadrastro_pessoas.tipoacesso.service;

import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
}

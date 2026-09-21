package com.empresa.cadrastro_pessoas.usuario.service;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.dto.CriarAcessoPessoaRequest;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Test
    void deveCriarAcessoVisitanteParaPessoaExistente() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PessoaRepository pessoaRepository = mock(PessoaRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UsuarioService service = new UsuarioService(usuarioRepository, pessoaRepository, passwordEncoder);

        Pessoa pessoa = Pessoa.builder()
                .id(10L)
                .nome("Maria")
                .email("Maria@Email.com")
                .ativo(true)
                .build();
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(pessoa));
        when(usuarioRepository.existsByPessoaId(10L)).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senha-protegida");
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenAnswer(invocacao -> {
                    Usuario usuario = invocacao.getArgument(0);
                    usuario.setId(20L);
                    return usuario;
                });

        var resposta = service.criarAcessoParaPessoa(
                10L,
                new CriarAcessoPessoaRequest("senha123", "senha123")
        );

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getPessoa()).isSameAs(pessoa);
        assertThat(salvo.getPerfil()).isEqualTo(Perfil.VISITANTE);
        assertThat(salvo.getEmail()).isEqualTo("maria@email.com");
        assertThat(salvo.getSenhaHash()).isEqualTo("senha-protegida");
        assertThat(resposta.pessoaId()).isEqualTo(10L);
    }

    @Test
    void naoDeveCriarSegundoAcessoParaMesmaPessoa() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PessoaRepository pessoaRepository = mock(PessoaRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UsuarioService service = new UsuarioService(usuarioRepository, pessoaRepository, passwordEncoder);

        Pessoa pessoa = Pessoa.builder()
                .id(10L)
                .email("maria@email.com")
                .ativo(true)
                .build();
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(pessoa));
        when(usuarioRepository.existsByPessoaId(10L)).thenReturn(true);

        assertThatThrownBy(() -> service.criarAcessoParaPessoa(
                10L,
                new CriarAcessoPessoaRequest("senha123", "senha123")
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já possui uma conta");
    }
}

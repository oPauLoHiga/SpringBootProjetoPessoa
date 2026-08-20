package com.empresa.cadrastro_pessoas.usuario.model;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    void devePrepararNovaContaAntesDeSalvar() {
        Usuario usuario = Usuario.builder()
                .email("  PAULO@EXEMPLO.COM ")
                .senhaHash("senha-ja-protegida")
                .perfil(Perfil.VISITANTE)
                .build();

        usuario.onCreate();

        assertThat(usuario.getEmail()).isEqualTo("paulo@exemplo.com");
        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getCriadoEm()).isNotNull();
        assertThat(usuario.getAtualizadoEm()).isEqualTo(usuario.getCriadoEm());
    }

    @Test
    void naoDeveReativarContaCriadaComoInativa() {
        Usuario usuario = Usuario.builder()
                .email("admin@exemplo.com")
                .senhaHash("senha-ja-protegida")
                .perfil(Perfil.ADMIN)
                .ativo(false)
                .build();

        usuario.onCreate();

        assertThat(usuario.getAtivo()).isFalse();
    }
}

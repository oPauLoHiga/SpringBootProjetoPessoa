package com.empresa.cadrastro_pessoas.usuario.controller;

import com.empresa.cadrastro_pessoas.auth.security.UsuarioPrincipal;
import com.empresa.cadrastro_pessoas.usuario.dto.AlterarPerfilRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.CriarUsuarioRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.RedefinirSenhaRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.UsuarioResponse;
import com.empresa.cadrastro_pessoas.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody CriarUsuarioRequest request) {
        return usuarioService.criarContaEquipe(request);
    }

    @PatchMapping("/{id}/perfil")
    public UsuarioResponse alterarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody AlterarPerfilRequest request,
            @AuthenticationPrincipal UsuarioPrincipal administrador
    ) {
        return usuarioService.alterarPerfil(id, request, administrador.id());
    }

    @PatchMapping("/{id}/ativar")
    public UsuarioResponse ativar(@PathVariable Long id) {
        return usuarioService.ativar(id);
    }

    @PatchMapping("/{id}/desativar")
    public UsuarioResponse desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal administrador
    ) {
        return usuarioService.desativar(id, administrador.id());
    }

    @PutMapping("/{id}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redefinirSenha(
            @PathVariable Long id,
            @Valid @RequestBody RedefinirSenhaRequest request
    ) {
        usuarioService.redefinirSenha(id, request);
    }
}

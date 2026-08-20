package com.empresa.cadrastro_pessoas.auth.controller;

import com.empresa.cadrastro_pessoas.auth.dto.CadastroVisitanteRequest;
import com.empresa.cadrastro_pessoas.auth.dto.CadastroVisitanteResponse;
import com.empresa.cadrastro_pessoas.auth.dto.SessaoResponse;
import com.empresa.cadrastro_pessoas.auth.security.UsuarioPrincipal;
import com.empresa.cadrastro_pessoas.auth.service.CadastroVisitanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final CadastroVisitanteService cadastroVisitanteService;

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping("/me")
    public SessaoResponse usuarioAtual(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return SessaoResponse.de(principal);
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroVisitanteResponse cadastrar(
            @Valid @RequestBody CadastroVisitanteRequest request
    ) {
        return cadastroVisitanteService.cadastrar(request);
    }
}

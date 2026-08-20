package com.empresa.cadrastro_pessoas.minhaconta.controller;

import com.empresa.cadrastro_pessoas.auth.security.UsuarioPrincipal;
import com.empresa.cadrastro_pessoas.minhaconta.dto.SugestaoPropriaRequest;
import com.empresa.cadrastro_pessoas.minhaconta.service.MinhaContaService;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/minha-conta")
@RequiredArgsConstructor
public class MinhaContaController {

    private final MinhaContaService minhaContaService;

    @GetMapping
    public PessoaResponse meusDados(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return minhaContaService.buscarPessoa(principal.pessoaId());
    }

    @GetMapping("/sugestoes")
    public List<SugestaoResponse> minhasSugestoes(
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return minhaContaService.listarSugestoes(principal.pessoaId());
    }

    @PostMapping("/sugestoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SugestaoResponse criarSugestao(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody SugestaoPropriaRequest request
    ) {
        return minhaContaService.criarSugestao(principal.pessoaId(), request);
    }
}

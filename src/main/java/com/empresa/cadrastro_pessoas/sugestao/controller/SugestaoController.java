package com.empresa.cadrastro_pessoas.sugestao.controller;

import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoRequest;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoResponse;
import com.empresa.cadrastro_pessoas.sugestao.service.SugestaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugestoes")
@CrossOrigin(origins = "http://localhost:5173")
public class SugestaoController {

    private final SugestaoService service;

    public SugestaoController(SugestaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<SugestaoResponse> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/por-status")
    public List<SugestaoResponse> listarPorStatus(
            @RequestParam StatusSugestao status) {
        return service.listarPorStatus(status);
    }

    @GetMapping("/pessoa/{pessoaId}")
    public List<SugestaoResponse> listarPorPessoa(@PathVariable Long pessoaId) {
        return service.listarPorPessoa(pessoaId);
    }

    @GetMapping("/{id}")
    public SugestaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SugestaoResponse criar(@RequestBody @Valid SugestaoRequest req) {
        return service.criar(req);
    }

    @PatchMapping("/{id}/status")
    public SugestaoResponse alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusSugestao novoStatus) {
        return service.alterarStatus(id, novoStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
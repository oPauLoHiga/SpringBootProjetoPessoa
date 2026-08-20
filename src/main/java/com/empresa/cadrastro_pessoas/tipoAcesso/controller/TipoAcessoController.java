package com.empresa.cadrastro_pessoas.tipoacesso.controller;

import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoacesso.service.TipoAcessoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-acesso")
public class TipoAcessoController {

    private final TipoAcessoService service;

    public TipoAcessoController(TipoAcessoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoAcessoResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/ativos")
    public List<TipoAcessoResponse> listarAtivos() {
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public TipoAcessoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoAcessoResponse criar(@RequestBody @Valid TipoAcessoRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public TipoAcessoResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TipoAcessoRequest req) {
        return service.atualizar(id, req);
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        service.desativar(id);
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long id) {
        service.ativar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}

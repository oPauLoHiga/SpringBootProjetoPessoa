package com.empresa.cadrastro_pessoas.tipoAcesso.controller;

import com.empresa.cadrastro_pessoas.tipoAcesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoAcesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoAcesso.service.TipoAcessoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-acesso")
@CrossOrigin(origins = "http://localhost:5173")
public class TipoAcessoController {

    private final TipoAcessoService service;

    public TipoAcessoController(TipoAcessoService service) {
        this.service = service;
    }

    // GET /api/tipos-acesso → lista todos
    @GetMapping
    public List<TipoAcessoResponse> listarTodos() {
        return service.listarTodos();
    }

    // GET /api/tipos-acesso/ativos → apenas ativos
    @GetMapping("/ativos")
    public List<TipoAcessoResponse> listarAtivos() {
        return service.listarAtivos();
    }

    // GET /api/tipos-acesso/{id} → busca por ID
    @GetMapping("/{id}")
    public TipoAcessoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // POST /api/tipos-acesso → criar novo
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoAcessoResponse criar(@RequestBody @Valid TipoAcessoRequest req) {
        return service.criar(req);
    }

    // PUT /api/tipos-acesso/{id} → atualizar
    @PutMapping("/{id}")
    public TipoAcessoResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TipoAcessoRequest req) {
        return service.atualizar(id, req);
    }

    // PATCH /api/tipos-acesso/{id}/desativar
    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        service.desativar(id);
    }

    // DELETE /api/tipos-acesso/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
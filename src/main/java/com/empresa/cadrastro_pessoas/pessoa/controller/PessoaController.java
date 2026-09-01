package com.empresa.cadrastro_pessoas.pessoa.controller;

import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaExclusaoResponse;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoa.service.PessoaService;
import com.empresa.cadrastro_pessoas.auth.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaResponse>> listarTodas() {
        return ResponseEntity.ok(pessoaService.listarTodas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<PessoaResponse>> listarAtivas() {
        return ResponseEntity.ok(pessoaService.listarAtivas());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PessoaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pessoaService.buscarPorCpf(cpf));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PessoaResponse>> buscarPorNome(
            @RequestParam String nome) {
        return ResponseEntity.ok(pessoaService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar(
            @Valid @RequestBody PessoaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaService.cadastrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaRequest dto) {
        return ResponseEntity.ok(pessoaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAtual
    ) {
        pessoaService.desativar(id, usuarioAtual.id());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        pessoaService.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resumo-exclusao")
    public ResponseEntity<PessoaExclusaoResponse> obterResumoExclusao(
            @PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.obterResumoExclusao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PessoaExclusaoResponse> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAtual
    ) {
        return ResponseEntity.ok(pessoaService.excluir(id, usuarioAtual.id()));
    }

}

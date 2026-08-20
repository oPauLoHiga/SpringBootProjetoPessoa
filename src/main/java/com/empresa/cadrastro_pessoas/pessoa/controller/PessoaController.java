package com.empresa.cadrastro_pessoas.pessoa.controller;

import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaExclusaoResponse;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> cadastrar(
            @Valid @RequestBody PessoaRequest dto) {
        Pessoa pessoaSalva = pessoaService.cadastrar(dto);
        return ResponseEntity.ok(pessoaSalva.getNome() + " cadastrado(a) com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaRequest dto) {
        pessoaService.atualizar(id, dto);
        return modificar(id);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<String> desativar(@PathVariable Long id) {
        pessoaService.desativar(id);
        return modificar(id);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<String> ativar(@PathVariable Long id) {
        pessoaService.ativar(id);
        return modificar(id);
    }

    @GetMapping("/{id}/resumo-exclusao")
    public ResponseEntity<PessoaExclusaoResponse> obterResumoExclusao(
            @PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.obterResumoExclusao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PessoaExclusaoResponse> excluir(@PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.excluir(id));
    }

    private ResponseEntity<String> modificar(Long id) {
        PessoaResponse pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa.getNome() + " modificado(a) com sucesso!");
    }
}

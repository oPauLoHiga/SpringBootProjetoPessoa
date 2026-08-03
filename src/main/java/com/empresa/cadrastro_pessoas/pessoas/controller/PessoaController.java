package com.empresa.cadrastro_pessoas.pessoas.controller;

import com.empresa.cadrastro_pessoas.pessoas.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoas.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoas.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                   // ← REST controller: cada metodo retorna JSON
@RequestMapping("/api/pessoas")     // ← URL base de todos os endpoints
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService pessoaService;

    // ==============================
    // GET /api/pessoas
    // Listar todas as pessoas
    // ==============================
    @GetMapping
    public ResponseEntity<List<Pessoa>> listarTodas() {
        List<Pessoa> pessoas = pessoaService.listarTodas();
        return ResponseEntity.ok(pessoas);
    }

    // ==============================
    // GET /api/pessoas/ativas
    // Listar apenas pessoas ativas
    // ==============================
    @GetMapping("/ativas")
    public ResponseEntity<List<Pessoa>> listarAtivas() {
        return ResponseEntity.ok(pessoaService.listarAtivas());
    }

    // ==============================
    // GET /api/pessoas/{id}
    // Buscar pessoa por ID
    // ==============================
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable Long id) {
        Pessoa pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }

    // ==============================
    // GET /api/pessoas/cpf/{cpf}
    // Buscar por CPF
    // ==============================
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Pessoa> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pessoaService.buscarPorCpf(cpf));
    }

    // ==============================
    // GET /api/pessoas/buscar?nome=João
    // Buscar por nome
    // ==============================
    @GetMapping("/buscar")
    public ResponseEntity<List<Pessoa>> buscarPorNome(
            @RequestParam String nome) {
        return ResponseEntity.ok(pessoaService.buscarPorNome(nome));
    }

    // ==============================
    // POST /api/pessoas
    // Cadastrar nova pessoa
    // ==============================
    @PostMapping
    public ResponseEntity<String> cadastrar(
            @Valid @RequestBody PessoaRequest dto) {
        Pessoa pessoaSalva = pessoaService.cadastrar(dto);
        return ResponseEntity.ok(pessoaSalva.getNome() + " cadastrado(a) com sucesso!");
    }

    // ==============================
    // PUT /api/pessoas/{id}
    // Atualizar pessoa completa
    // ==============================
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaRequest dto) {
        pessoaService.atualizar(id, dto);
        return modificar(id);
    }

    // ==============================
    // PATCH /api/pessoas/{id}/desativar
    // Desativar (soft delete)
    // ==============================
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<String> desativar(
            @PathVariable Long id) {
        pessoaService.desativar(id);

        return modificar(id); // 204 No Content
    }
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<String> ativar(@PathVariable Long id) {
        pessoaService.ativar(id);

        return modificar(id); // 204 No Content
    }

    // ==============================
    // DELETE /api/pessoas/{id}
    // Excluir permanentemente
    // ==============================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        Pessoa excluido = pessoaService.excluir(id);

        return ResponseEntity.ok(excluido.getNome() + " excluido com sucesso!") ;// 204 No Content
    }

    public ResponseEntity<String> modificar(@PathVariable Long id){
        Pessoa modificar = pessoaService.buscarPorId(id);
        return ResponseEntity.ok( modificar.getNome()+" modificado(a) com sucesso!");
    }
}
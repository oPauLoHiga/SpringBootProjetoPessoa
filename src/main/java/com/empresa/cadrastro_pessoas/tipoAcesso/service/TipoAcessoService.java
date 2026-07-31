package com.empresa.cadrastro_pessoas.tipoAcesso.service;

import com.empresa.cadrastro_pessoas.tipoAcesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoAcesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoAcesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoAcesso.repository.TipoAcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TipoAcessoService {

    private final TipoAcessoRepository repository;

    // Injeção de dependência via construtor (melhor prática)
    public TipoAcessoService(TipoAcessoRepository repository) {
        this.repository = repository;
    }

    // ── LISTAR TODOS ──────────────────────────────────────
    public List<TipoAcessoResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    // ── LISTAR ATIVOS ─────────────────────────────────────
    public List<TipoAcessoResponse> listarAtivos() {
        return repository.findByAtivoTrue()
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    // ── BUSCAR POR ID ─────────────────────────────────────
    public TipoAcessoResponse buscarPorId(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso não encontrado: " + id));
        return TipoAcessoResponse.de(tipo);
    }

    // ── CRIAR ─────────────────────────────────────────────
    @Transactional
    public TipoAcessoResponse criar(TipoAcessoRequest req) {
        if (repository.existsByNomeIgnoreCase(req.getNome())) {
            throw new RuntimeException("Já existe um TipoAcesso com o nome: " + req.getNome());
        }
        TipoAcesso tipo = new TipoAcesso(req.getNome(), req.getDescricao());
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    // ── ATUALIZAR ─────────────────────────────────────────
    @Transactional
    public TipoAcessoResponse atualizar(Long id, TipoAcessoRequest req) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso não encontrado: " + id));
        tipo.setNome(req.getNome());
        tipo.setDescricao(req.getDescricao());
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    // ── DESATIVAR ─────────────────────────────────────────
    @Transactional
    public void desativar(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso não encontrado: " + id));
        tipo.setAtivo(false);
        repository.save(tipo);
    }

    // ── EXCLUIR ───────────────────────────────────────────
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("TipoAcesso não encontrado: " + id);
        }
        repository.deleteById(id);
    }
}

package com.empresa.cadrastro_pessoas.tipoacesso.service;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TipoAcessoService {

    private final TipoAcessoRepository repository;

    // InjeÃ§Ã£o de dependÃªncia via construtor (melhor prÃ¡tica)
    public TipoAcessoService(TipoAcessoRepository repository) {
        this.repository = repository;
    }

    // â”€â”€ LISTAR TODOS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public List<TipoAcessoResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    // â”€â”€ LISTAR ATIVOS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public List<TipoAcessoResponse> listarAtivos() {
        return repository.findByAtivoTrue()
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    // â”€â”€ BUSCAR POR ID â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public TipoAcessoResponse buscarPorId(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso nÃ£o encontrado: " + id));
        return TipoAcessoResponse.de(tipo);
    }

    // â”€â”€ CRIAR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Transactional
    public TipoAcessoResponse criar(TipoAcessoRequest req) {
        if (repository.existsByNomeIgnoreCase(req.getNome())) {
            throw new RuntimeException("JÃ¡ existe um TipoAcesso com o nome: " + req.getNome());
        }
        TipoAcesso tipo = new TipoAcesso(req.getNome(), req.getDescricao());
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    // â”€â”€ ATUALIZAR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Transactional
    public TipoAcessoResponse atualizar(Long id, TipoAcessoRequest req) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso nÃ£o encontrado: " + id));
        tipo.setNome(req.getNome());
        tipo.setDescricao(req.getDescricao());
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    // â”€â”€ DESATIVAR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Transactional
    public void desativar(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso nÃ£o encontrado: " + id));
        tipo.setAtivo(false);
        repository.save(tipo);
    }

    @Transactional
    public void ativar(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoAcesso nÃ£o encontrado: " + id));
        tipo.setAtivo(true);
        repository.save(tipo);
    }

    // â”€â”€ EXCLUIR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("TipoAcesso nÃ£o encontrado: " + id);
        }
        repository.deleteById(id);
    }
}

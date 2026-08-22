package com.empresa.cadrastro_pessoas.tipoacesso.service;

import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
public class TipoAcessoService {

    private static final String TIPO_PADRAO = "Visitante";

    private final TipoAcessoRepository repository;

    public TipoAcessoService(TipoAcessoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoAcessoResponse> listarTodos() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome"))
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TipoAcessoResponse> listarAtivos() {
        return repository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(TipoAcessoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoAcessoResponse buscarPorId(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de acesso não encontrado: " + id));
        return TipoAcessoResponse.de(tipo);
    }

    @Transactional
    public TipoAcessoResponse criar(TipoAcessoRequest req) {
        String nome = req.getNome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new BusinessException("Já existe um tipo de acesso com o nome: " + nome);
        }
        TipoAcesso tipo = new TipoAcesso(nome, normalizarOpcional(req.getDescricao()));
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    @Transactional
    public TipoAcessoResponse atualizar(Long id, TipoAcessoRequest req) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de acesso não encontrado: " + id));
        String nome = req.getNome().trim();
        if (ehTipoPadrao(tipo) && !TIPO_PADRAO.equalsIgnoreCase(nome)) {
            throw new BusinessException("O tipo Visitante é obrigatório e não pode ser renomeado.");
        }
        repository.findByNomeIgnoreCase(nome).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new BusinessException("Já existe um tipo de acesso com o nome: " + nome);
            }
        });
        tipo.setNome(nome);
        tipo.setDescricao(normalizarOpcional(req.getDescricao()));
        return TipoAcessoResponse.de(repository.save(tipo));
    }

    @Transactional
    public void desativar(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de acesso não encontrado: " + id));
        if (ehTipoPadrao(tipo)) {
            throw new BusinessException("O tipo Visitante é obrigatório e não pode ser desativado.");
        }
        tipo.setAtivo(false);
        repository.save(tipo);
    }

    @Transactional
    public void ativar(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de acesso não encontrado: " + id));
        tipo.setAtivo(true);
        repository.save(tipo);
    }

    @Transactional
    public void excluir(Long id) {
        TipoAcesso tipo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de acesso não encontrado: " + id));
        if (ehTipoPadrao(tipo)) {
            throw new BusinessException("O tipo Visitante é obrigatório e não pode ser excluído.");
        }
        if (tipo.getPessoas() != null && !tipo.getPessoas().isEmpty()) {
            throw new BusinessException("Não é possível excluir um tipo vinculado a pessoas.");
        }
        repository.delete(tipo);
    }

    private boolean ehTipoPadrao(TipoAcesso tipo) {
        return TIPO_PADRAO.equalsIgnoreCase(tipo.getNome());
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

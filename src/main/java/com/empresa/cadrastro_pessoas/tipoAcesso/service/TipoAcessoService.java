package com.empresa.cadrastro_pessoas.tipoacesso.service;

import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoRequest;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class TipoAcessoService {
    private static final String CATEGORIA_PADRAO = "VISITANTE";

    private final TipoAcessoRepository repository;

    public TipoAcessoService(TipoAcessoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoAcessoResponse> listarTodos() {
        return repository.listarTodosComTotalPessoas();
    }

    @Transactional(readOnly = true)
    public List<TipoAcessoResponse> listarAtivos() {
        return repository.listarAtivosComTotalPessoas()
                .stream()
                .filter(tipo -> tipo.getPerfil() == null || tipo.getPerfil() == Perfil.VISITANTE)
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
        String nome = normalizarNome(req.getNome());
        if (ehNomeDePerfil(nome)) {
            throw new BusinessException("ADMIN, OPERADOR e VISITANTE são nomes reservados para perfis do sistema.");
        }
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
        String nome = normalizarNome(req.getNome());
        if (ehCategoriaPadrao(tipo) && !tipo.getNome().equalsIgnoreCase(nome)) {
            throw new BusinessException("A categoria VISITANTE é obrigatória e não pode ser renomeada.");
        }
        if (ehNomeDePerfil(nome) && !tipo.getNome().equalsIgnoreCase(nome)) {
            throw new BusinessException("ADMIN, OPERADOR e VISITANTE são nomes reservados para perfis do sistema.");
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
        if (ehCategoriaPadrao(tipo)) {
            throw new BusinessException("A categoria VISITANTE é obrigatória e não pode ser desativada.");
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
        if (ehCategoriaPadrao(tipo)) {
            throw new BusinessException("A categoria VISITANTE é obrigatória e não pode ser excluída.");
        }
        if (tipo.getPessoas() != null && !tipo.getPessoas().isEmpty()) {
            throw new BusinessException("Não é possível excluir um tipo vinculado a pessoas.");
        }
        repository.delete(tipo);
    }

    private boolean ehNomeDePerfil(String nome) {
        return Arrays.stream(Perfil.values())
                .anyMatch(perfil -> perfil.name().equalsIgnoreCase(nome));
    }

    private boolean ehCategoriaPadrao(TipoAcesso tipo) {
        return CATEGORIA_PADRAO.equalsIgnoreCase(tipo.getNome());
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String normalizarNome(String nome) {
        String valor = nome == null ? "" : nome.trim();
        if (valor.length() < 2) {
            throw new BusinessException("Nome deve ter pelo menos 2 caracteres.");
        }
        return valor;
    }
}

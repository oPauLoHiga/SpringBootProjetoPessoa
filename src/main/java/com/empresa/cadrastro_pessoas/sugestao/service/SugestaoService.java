package com.empresa.cadrastro_pessoas.sugestao.service;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.sugestao.*;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoRequest;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoResponse;
import com.empresa.cadrastro_pessoas.sugestao.model.Sugestao;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SugestaoService {

    private final SugestaoRepository sugestaoRepository;
    private final PessoaRepository pessoaRepository;

    public SugestaoService(SugestaoRepository sugestaoRepository,
                           PessoaRepository pessoaRepository) {
        this.sugestaoRepository = sugestaoRepository;
        this.pessoaRepository   = pessoaRepository;
    }

    @Transactional(readOnly = true)
    public List<SugestaoResponse> listarTodas() {
        return sugestaoRepository.findAll()
                .stream().map(SugestaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<SugestaoResponse> listarPorStatus(StatusSugestao status) {
        return sugestaoRepository.findByStatus(status)
                .stream().map(SugestaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<SugestaoResponse> listarPorPessoa(Long pessoaId) {
        return sugestaoRepository.findByPessoaId(pessoaId)
                .stream().map(SugestaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public SugestaoResponse buscarPorId(Long id) {
        Sugestao s = sugestaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sugestão não encontrada: " + id));
        return SugestaoResponse.de(s);
    }
    @Transactional
    public SugestaoResponse criar(SugestaoRequest req) {
        return criarParaPessoa(req.getPessoaId(), req.getTitulo(), req.getDescricao());
    }

    @Transactional
    public SugestaoResponse criarParaPessoa(Long pessoaId, String titulo, String descricao) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pessoa não encontrada: " + pessoaId));

        Sugestao sugestao = new Sugestao();
        sugestao.setTitulo(titulo.trim());
        sugestao.setDescricao(descricao.trim());
        sugestao.setPessoa(pessoa);
        // status inicia como PENDENTE (definido como default na entidade)

        return SugestaoResponse.de(sugestaoRepository.save(sugestao));
    }

    @Transactional
    public SugestaoResponse alterarStatus(Long id, StatusSugestao novoStatus) {
        Sugestao s = sugestaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sugestão não encontrada: " + id));
        s.setStatus(novoStatus);
        return SugestaoResponse.de(sugestaoRepository.save(s));
    }

    @Transactional
    public void excluir(Long id) {
        if (!sugestaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sugestão não encontrada: " + id);
        }
        sugestaoRepository.deleteById(id);
    }
}

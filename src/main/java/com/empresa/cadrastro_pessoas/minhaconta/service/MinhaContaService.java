package com.empresa.cadrastro_pessoas.minhaconta.service;

import com.empresa.cadrastro_pessoas.minhaconta.dto.SugestaoPropriaRequest;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.sugestao.dto.SugestaoResponse;
import com.empresa.cadrastro_pessoas.sugestao.service.SugestaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinhaContaService {

    private final PessoaRepository pessoaRepository;
    private final SugestaoService sugestaoService;

    @Transactional(readOnly = true)
    public PessoaResponse buscarPessoa(Long pessoaId) {
        Pessoa pessoa = buscarPessoaVinculada(pessoaId);
        return PessoaResponse.de(pessoa);
    }

    @Transactional(readOnly = true)
    public List<SugestaoResponse> listarSugestoes(Long pessoaId) {
        validarVinculo(pessoaId);
        return sugestaoService.listarPorPessoa(pessoaId);
    }

    @Transactional
    public SugestaoResponse criarSugestao(Long pessoaId, SugestaoPropriaRequest request) {
        validarVinculo(pessoaId);
        return sugestaoService.criarParaPessoa(pessoaId, request.titulo(), request.descricao());
    }

    private Pessoa buscarPessoaVinculada(Long pessoaId) {
        validarVinculo(pessoaId);
        return pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa vinculada não encontrada."));
    }

    private void validarVinculo(Long pessoaId) {
        if (pessoaId == null) {
            throw new BusinessException("Esta conta não possui uma pessoa vinculada.");
        }
    }
}

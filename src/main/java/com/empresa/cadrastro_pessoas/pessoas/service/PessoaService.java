package com.empresa.cadrastro_pessoas.pessoas.service;

import com.empresa.cadrastro_pessoas.exeption.BusinessException;
import com.empresa.cadrastro_pessoas.exeption.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.pessoas.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoas.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoas.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoas.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.tipoAcesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoAcesso.repository.TipoAcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final TipoAcessoRepository tipoAcessoRepository;

    @Transactional(readOnly = true)
    public List<PessoaResponse> listarTodas() {
        List<Pessoa> pessoas = pessoaRepository.findAll();

        if (pessoas.isEmpty()) {
            throw new BusinessException("Não foram encontradas pessoas");
        }

        return pessoas.stream()
                .map(PessoaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PessoaResponse> listarAtivas() {
        return pessoaRepository.findByAtivoTrue()
                .stream()
                .map(PessoaResponse::de)
                .toList();
    }

    private Pessoa buscarEntidadePorId(Long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pessoa não encontrada com ID: " + id)
                );
    }

    @Transactional(readOnly = true)
    public PessoaResponse buscarPorId(Long id) {
        return PessoaResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public PessoaResponse buscarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pessoa não encontrada com CPF: " + cpf)
                );

        return PessoaResponse.de(pessoa);
    }

    @Transactional(readOnly = true)
    public List<PessoaResponse> buscarPorNome(String nome) {
        return pessoaRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(PessoaResponse::de)
                .toList();
    }

    @Transactional
    public Pessoa cadastrar(PessoaRequest dto) {
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        }

        if (pessoaRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.getEmail());
        }

        Pessoa pessoa = Pessoa.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .endereco(dto.getEndereco())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .ativo(true)
                .build();

        if (dto.getTipoAcessoId() != null) {
            TipoAcesso tipo = tipoAcessoRepository.findById(dto.getTipoAcessoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de acesso não encontrado: " + dto.getTipoAcessoId()));
            pessoa.setTipoAcesso(tipo);
        }

        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa atualizar(Long id, PessoaRequest dto) {
        Pessoa pessoa = buscarEntidadePorId(id);

        pessoaRepository.findByCpf(dto.getCpf())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("CPF já cadastrado para outra pessoa.");
                    }
                });

        pessoaRepository.findByEmail(dto.getEmail())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("E-mail já cadastrado para outra pessoa.");
                    }
                });

        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setEmail(dto.getEmail());
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setEndereco(dto.getEndereco());
        pessoa.setCidade(dto.getCidade());
        pessoa.setEstado(dto.getEstado());

        if (dto.getTipoAcessoId() != null) {
            TipoAcesso tipo = tipoAcessoRepository.findById(dto.getTipoAcessoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de acesso não encontrado: " + dto.getTipoAcessoId()));
            pessoa.setTipoAcesso(tipo);
        }

        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public void desativar(Long id) {
        Pessoa pessoa = buscarEntidadePorId(id);
        pessoa.setAtivo(false);
        pessoaRepository.save(pessoa);
    }

    @Transactional
    public void ativar(Long id) {
        Pessoa pessoa = buscarEntidadePorId(id);
        pessoa.setAtivo(true);
        pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa excluir(Long id) {
        Pessoa pessoa = buscarEntidadePorId(id);
        pessoaRepository.delete(pessoa);
        return pessoa;
    }
}
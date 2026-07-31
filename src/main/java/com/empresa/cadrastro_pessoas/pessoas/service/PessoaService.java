package com.empresa.cadrastro_pessoas.pessoas.service;


import com.empresa.cadrastro_pessoas.pessoas.dto.PessoaDTO;
import com.empresa.cadrastro_pessoas.exeption.BusinessException;
import com.empresa.cadrastro_pessoas.exeption.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.pessoas.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoas.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service                 // ← Marca como componente de serviço Spring
@RequiredArgsConstructor // ← Lombok: injeta dependências pelo construtor
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    @Transactional()
    public List<Pessoa> listarTodas() {
        List<Pessoa> pessoas = (List<Pessoa>) pessoaRepository.findAll();
        if (pessoas.isEmpty()) {
            throw new BusinessException("Não foi encontrados pessoas");
        }
        return pessoas;
    }

    @Transactional()
    public List<Pessoa> listarAtivas() {
        return pessoaRepository.findByAtivoTrue();
    }


    @Transactional()
    public Pessoa buscarPorId(Long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pessoa não encontrada com ID: " + id)
                );
    }


    @Transactional()
    public Pessoa buscarPorCpf(String cpf) {

        return pessoaRepository.findByCpf(cpf)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pessoa não encontrada com CPF: " + cpf)
                );
        }


    @Transactional()
    public List<Pessoa> buscarPorNome(String nome) {
        return pessoaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Pessoa cadastrar(PessoaDTO dto) {

        // Validação: CPF já cadastrado?
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        }

        // Validação: E-mail já cadastrado?
        if (pessoaRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.getEmail());
        }

        // Converter DTO → Entidade
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

            // Salvar no banco e retornar a entidade salva (com ID gerado)
            return pessoaRepository.save(pessoa);
        }

    @Transactional
    public Pessoa atualizar(Long id, PessoaDTO dto) {

        // Busca a pessoa (lança exceção se não existir)
        Pessoa pessoa = buscarPorId(id);

        // Verifica se o novo CPF pertence a outra pessoa
        pessoaRepository.findByCpf(dto.getCpf())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("CPF já cadastrado para outra pessoa.");
                    }
                });

        // Verifica se o novo e-mail pertence a outra pessoa
        pessoaRepository.findByEmail(dto.getEmail())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("E-mail já cadastrado para outra pessoa.");
                    }
                });

        // Atualiza os campos
        if (dto.getNome() != null) {
            pessoa.setNome(dto.getNome());
        }
        if (dto.getCpf() != null) {
            pessoa.setCpf(dto.getCpf());
        }
        if (dto.getEmail() != null) {
            pessoa.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null) {
            pessoa.setTelefone(dto.getTelefone());
        }
        if (dto.getDataNascimento() != null) {
            pessoa.setDataNascimento(dto.getDataNascimento());
        }
        if (dto.getEndereco() != null) {
            pessoa.setEndereco(dto.getEndereco());
        }
        if (dto.getCidade() != null) {
            pessoa.setCidade(dto.getCidade());
        };
        if (dto.getEstado() != null) {
            pessoa.setEstado(dto.getEstado());
        }
        // save() com ID existente faz UPDATE (não INSERT)
        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public void desativar(Long id) {
            Pessoa pessoa = buscarPorId(id);
            pessoa.setAtivo(false);
            pessoaRepository.save(pessoa);
    }

    @Transactional
    public void ativar(Long id) {
        Pessoa pessoa = buscarPorId(id);
        pessoa.setAtivo(true);
        pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa excluir(Long id) {
            Pessoa pessoa = buscarPorId(id);
            pessoaRepository.delete(pessoa);
        return pessoa;
    }

}

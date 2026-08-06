package com.empresa.cadrastro_pessoas.pessoa.service;

import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
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
            throw new BusinessException("NÃ£o foram encontradas pessoas");
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
                        new ResourceNotFoundException("Pessoa nÃ£o encontrada com ID: " + id)
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
                        new ResourceNotFoundException("Pessoa nÃ£o encontrada com CPF: " + cpf)
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
            throw new BusinessException("CPF jÃ¡ cadastrado: " + dto.getCpf());
        }

        if (pessoaRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail jÃ¡ cadastrado: " + dto.getEmail());
        }

        Pessoa pessoa = Pessoa.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(normalizarTelefone(dto.getTelefone()))
                .dataNascimento(dto.getDataNascimento())
                .endereco(dto.getEndereco())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .ativo(true)
                .build();

        if (dto.getTipoAcessoId() != null) {
            TipoAcesso tipo = tipoAcessoRepository.findById(dto.getTipoAcessoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de acesso nÃ£o encontrado: " + dto.getTipoAcessoId()));
            pessoa.setTipoAcesso(tipo);
        }

        if (dto.getTipoAcessoId() == null) {
            TipoAcesso visitante = tipoAcessoRepository.findByNomeIgnoreCase("Visitante")
                    .orElseThrow(() -> new BusinessException("Tipo de acesso padrÃ£o nÃ£o configurado."));
            pessoa.setTipoAcesso(visitante);
        }

        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa atualizar(Long id, PessoaRequest dto) {
        Pessoa pessoa = buscarEntidadePorId(id);

        pessoaRepository.findByCpf(dto.getCpf())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("CPF jÃ¡ cadastrado para outra pessoa.");
                    }
                });

        pessoaRepository.findByEmail(dto.getEmail())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("E-mail jÃ¡ cadastrado para outra pessoa.");
                    }
                });

        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setEmail(dto.getEmail());
        pessoa.setTelefone(normalizarTelefone(dto.getTelefone()));
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setEndereco(dto.getEndereco());
        pessoa.setCidade(dto.getCidade());
        pessoa.setEstado(dto.getEstado());

        if (dto.getTipoAcessoId() != null) {
            TipoAcesso tipo = tipoAcessoRepository.findById(dto.getTipoAcessoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de acesso nÃ£o encontrado: " + dto.getTipoAcessoId()));
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

    private String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        String numeros = telefone.replaceAll("\\D", "");
        if (numeros.length() != 10 && numeros.length() != 11) {
            throw new BusinessException("Telefone deve ter 10 ou 11 dÃ­gitos.");
        }
        return numeros;
    }
}

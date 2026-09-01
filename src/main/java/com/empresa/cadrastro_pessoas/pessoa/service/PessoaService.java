package com.empresa.cadrastro_pessoas.pessoa.service;

import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaExclusaoResponse;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaRequest;
import com.empresa.cadrastro_pessoas.pessoa.dto.PessoaResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.sugestao.repository.SugestaoRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final TipoAcessoRepository tipoAcessoRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PessoaResponse> listarTodas() {
        return pessoaRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(PessoaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PessoaResponse> listarAtivas() {
        return pessoaRepository.findByAtivoTrueOrderByNomeAsc()
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
        String termo = nome == null ? "" : nome.trim();
        if (termo.length() < 2) {
            throw new BusinessException("Informe pelo menos 2 caracteres para buscar por nome.");
        }
        return pessoaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(termo)
                .stream()
                .map(PessoaResponse::de)
                .toList();
    }

    @Transactional
    public PessoaResponse cadastrar(PessoaRequest dto) {
        String email = normalizarEmail(dto.getEmail());
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.getCpf());
        }

        if (pessoaRepository.existsByEmailIgnoreCase(email)
                || usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("E-mail já cadastrado: " + email);
        }

        Pessoa pessoa = Pessoa.builder()
                .nome(normalizarNome(dto.getNome()))
                .cpf(dto.getCpf())
                .email(email)
                .telefone(normalizarTelefone(dto.getTelefone()))
                .dataNascimento(dto.getDataNascimento())
                .endereco(normalizarOpcional(dto.getEndereco()))
                .cidade(normalizarOpcional(dto.getCidade()))
                .estado(normalizarEstado(dto.getEstado()))
                .ativo(true)
                .build();

        if (dto.getTipoAcessoId() != null) {
            pessoa.setTipoAcesso(buscarTipoAtivo(dto.getTipoAcessoId()));
        }

        if (dto.getTipoAcessoId() == null) {
            TipoAcesso visitante = tipoAcessoRepository.findByNomeIgnoreCase("Visitante")
                    .orElseThrow(() -> new BusinessException("Tipo de acesso padrão não configurado."));
            if (!visitante.isAtivo()) {
                throw new BusinessException("O tipo de acesso padrão Visitante está inativo.");
            }
            pessoa.setTipoAcesso(visitante);
        }

        return PessoaResponse.de(pessoaRepository.save(pessoa));
    }

    @Transactional
    public PessoaResponse atualizar(Long id, PessoaRequest dto) {
        Pessoa pessoa = buscarEntidadePorId(id);
        String email = normalizarEmail(dto.getEmail());

        pessoaRepository.findByCpf(dto.getCpf())
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("CPF já cadastrado para outra pessoa.");
                    }
                });

        pessoaRepository.findByEmailIgnoreCase(email)
                .ifPresent(outra -> {
                    if (!outra.getId().equals(id)) {
                        throw new BusinessException("E-mail já cadastrado para outra pessoa.");
                    }
                });

        usuarioRepository.findByEmailIgnoreCase(email)
                .ifPresent(usuario -> {
                    if (usuario.getPessoa() == null || !usuario.getPessoa().getId().equals(id)) {
                        throw new BusinessException("E-mail já pertence a outra conta de acesso.");
                    }
                });

        pessoa.setNome(normalizarNome(dto.getNome()));
        pessoa.setCpf(dto.getCpf());
        pessoa.setEmail(email);
        pessoa.setTelefone(normalizarTelefone(dto.getTelefone()));
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setEndereco(normalizarOpcional(dto.getEndereco()));
        pessoa.setCidade(normalizarOpcional(dto.getCidade()));
        pessoa.setEstado(normalizarEstado(dto.getEstado()));

        usuarioRepository.findByPessoaId(id).ifPresent(usuario -> {
            usuario.setEmail(email);
            usuarioRepository.save(usuario);
        });

        if (dto.getTipoAcessoId() != null) {
            pessoa.setTipoAcesso(buscarTipoAtivo(dto.getTipoAcessoId()));
        }

        return PessoaResponse.de(pessoaRepository.save(pessoa));
    }

    @Transactional
    public void desativar(Long id, Long usuarioAtualId) {
        Pessoa pessoa = buscarEntidadePorId(id);
        validarNaoEhProprioCadastro(id, usuarioAtualId,
                "Você não pode desativar a pessoa vinculada à sua própria conta.");
        pessoa.setAtivo(false);
        pessoaRepository.save(pessoa);
    }

    @Transactional
    public void ativar(Long id) {
        Pessoa pessoa = buscarEntidadePorId(id);
        pessoa.setAtivo(true);
        pessoaRepository.save(pessoa);
    }

    @Transactional(readOnly = true)
    public PessoaExclusaoResponse obterResumoExclusao(Long id) {
        Pessoa pessoa = buscarEntidadePorId(id);
        return criarResumoExclusao(pessoa);
    }

    @Transactional
    public PessoaExclusaoResponse excluir(Long id, Long usuarioAtualId) {
        Pessoa pessoa = buscarEntidadePorId(id);
        validarNaoEhProprioCadastro(id, usuarioAtualId,
                "Você não pode excluir a pessoa vinculada à sua própria conta.");
        PessoaExclusaoResponse resumo = criarResumoExclusao(pessoa);

        usuarioRepository.deleteByPessoaId(id);
        sugestaoRepository.deleteByPessoaId(id);
        pessoaRepository.delete(pessoa);

        return resumo;
    }

    private PessoaExclusaoResponse criarResumoExclusao(Pessoa pessoa) {
        long totalSugestoes = sugestaoRepository.countByPessoaId(pessoa.getId());
        boolean contaVinculada = usuarioRepository.existsByPessoaId(pessoa.getId());
        return new PessoaExclusaoResponse(
                pessoa.getId(),
                pessoa.getNome(),
                totalSugestoes,
                contaVinculada
        );
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        String numeros = telefone.replaceAll("\\D", "");
        if (numeros.length() != 10 && numeros.length() != 11) {
            throw new BusinessException("Telefone deve ter 10 ou 11 dígitos.");
        }
        return numeros;
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarNome(String nome) {
        String valor = nome == null ? "" : nome.trim();
        if (valor.length() < 2) {
            throw new BusinessException("Nome deve ter pelo menos 2 caracteres.");
        }
        return valor;
    }

    private String normalizarEstado(String estado) {
        String valor = normalizarOpcional(estado);
        return valor == null ? null : valor.toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private TipoAcesso buscarTipoAtivo(Long id) {
        TipoAcesso tipo = tipoAcessoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de acesso não encontrado: " + id));
        if (!tipo.isAtivo()) {
            throw new BusinessException("Não é possível vincular uma pessoa a um tipo de acesso inativo.");
        }
        return tipo;
    }

    private void validarNaoEhProprioCadastro(Long pessoaId, Long usuarioAtualId, String mensagem) {
        usuarioRepository.findByPessoaId(pessoaId).ifPresent(usuario -> {
            if (usuario.getId().equals(usuarioAtualId)) {
                throw new BusinessException(mensagem);
            }
        });
    }
}

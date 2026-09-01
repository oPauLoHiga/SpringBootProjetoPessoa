package com.empresa.cadrastro_pessoas.auth.service;

import com.empresa.cadrastro_pessoas.auth.dto.CadastroVisitanteRequest;
import com.empresa.cadrastro_pessoas.auth.dto.CadastroVisitanteResponse;
import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CadastroVisitanteService {

    private final PessoaRepository pessoaRepository;
    private final TipoAcessoRepository tipoAcessoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CadastroVisitanteResponse cadastrar(CadastroVisitanteRequest request) {
        validarSenhas(request.getSenha(), request.getConfirmacaoSenha());

        String email = normalizarEmail(request.getEmail());
        if (pessoaRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }
        if (pessoaRepository.existsByEmailIgnoreCase(email)
                || usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        TipoAcesso tipoVisitante = tipoAcessoRepository.findByNomeIgnoreCase("Visitante")
                .orElseThrow(() -> new BusinessException("Tipo de acesso padrão não configurado."));

        Pessoa pessoa = Pessoa.builder()
                .nome(normalizarNome(request.getNome()))
                .cpf(request.getCpf())
                .email(email)
                .telefone(normalizarTelefone(request.getTelefone()))
                .dataNascimento(request.getDataNascimento())
                .endereco(normalizarOpcional(request.getEndereco()))
                .cidade(normalizarOpcional(request.getCidade()))
                .estado(normalizarEstado(request.getEstado()))
                .ativo(true)
                .tipoAcesso(tipoVisitante)
                .build();
        pessoa = pessoaRepository.save(pessoa);

        Usuario usuario = Usuario.builder()
                .email(email)
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .perfil(Perfil.VISITANTE)
                .ativo(true)
                .pessoa(pessoa)
                .build();
        usuario = usuarioRepository.save(usuario);

        return new CadastroVisitanteResponse(
                usuario.getId(),
                pessoa.getId(),
                pessoa.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }

    private void validarSenhas(String senha, String confirmacao) {
        if (!senha.equals(confirmacao)) {
            throw new BusinessException("A senha e a confirmação não são iguais.");
        }
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

    private String normalizarEstado(String estado) {
        String valor = normalizarOpcional(estado);
        return valor == null ? null : valor.toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

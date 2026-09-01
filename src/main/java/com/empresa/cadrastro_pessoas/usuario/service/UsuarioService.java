package com.empresa.cadrastro_pessoas.usuario.service;

import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.shared.exception.BusinessException;
import com.empresa.cadrastro_pessoas.shared.exception.ResourceNotFoundException;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.dto.AlterarPerfilRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.CriarUsuarioRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.RedefinirSenhaRequest;
import com.empresa.cadrastro_pessoas.usuario.dto.UsuarioResponse;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByEmailAsc()
                .stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    @Transactional
    public UsuarioResponse criarContaEquipe(CriarUsuarioRequest request) {
        validarSenhas(request.getSenha(), request.getConfirmacaoSenha());
        if (request.getPerfil() == Perfil.VISITANTE) {
            throw new BusinessException("Contas de visitante devem ser criadas pelo cadastro público.");
        }

        String email = normalizarEmail(request.getEmail());
        if (usuarioRepository.existsByEmailIgnoreCase(email)
                || pessoaRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .email(email)
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .perfil(request.getPerfil())
                .ativo(true)
                .build();
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse alterarPerfil(
            Long id,
            AlterarPerfilRequest request,
            Long administradorAtualId
    ) {
        Usuario usuario = buscar(id);
        if (id.equals(administradorAtualId) && request.perfil() != Perfil.ADMIN) {
            throw new BusinessException("Você não pode remover o seu próprio perfil de administrador.");
        }
        if (request.perfil() == Perfil.VISITANTE && usuario.getPessoa() == null) {
            throw new BusinessException("Um visitante precisa estar vinculado a uma pessoa.");
        }

        usuario.setPerfil(request.perfil());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse ativar(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getPessoa() != null && !Boolean.TRUE.equals(usuario.getPessoa().getAtivo())) {
            throw new BusinessException("Ative primeiro o cadastro da pessoa vinculada.");
        }
        usuario.setAtivo(true);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse desativar(Long id, Long administradorAtualId) {
        if (id.equals(administradorAtualId)) {
            throw new BusinessException("Você não pode desativar a própria conta.");
        }
        Usuario usuario = buscar(id);
        usuario.setAtivo(false);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public void redefinirSenha(Long id, RedefinirSenhaRequest request) {
        validarSenhas(request.senha(), request.confirmacaoSenha());
        Usuario usuario = buscar(id);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuarioRepository.save(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findByIdComPessoa(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private void validarSenhas(String senha, String confirmacao) {
        if (!senha.equals(confirmacao)) {
            throw new BusinessException("A senha e a confirmação não são iguais.");
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

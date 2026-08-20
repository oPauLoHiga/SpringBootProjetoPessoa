package com.empresa.cadrastro_pessoas.config;

import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInicialInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String senha;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.existsByPerfil(Perfil.ADMIN)) {
            return;
        }
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            log.warn("Nenhum administrador existe. Configure APP_ADMIN_EMAIL e APP_ADMIN_PASSWORD para criar o primeiro acesso.");
            return;
        }
        if (senha.length() < 8 || senha.length() > 72) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD deve ter entre 8 e 72 caracteres.");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new IllegalStateException("APP_ADMIN_EMAIL já pertence a outra conta.");
        }

        usuarioRepository.save(Usuario.builder()
                .email(emailNormalizado)
                .senhaHash(passwordEncoder.encode(senha))
                .perfil(Perfil.ADMIN)
                .ativo(true)
                .build());
        log.info("Administrador inicial criado para o e-mail configurado.");
    }
}

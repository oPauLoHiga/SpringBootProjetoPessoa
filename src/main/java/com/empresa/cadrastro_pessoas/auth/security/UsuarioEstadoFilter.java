package com.empresa.cadrastro_pessoas.auth.security;

import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UsuarioEstadoFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao != null
                && autenticacao.isAuthenticated()
                && autenticacao.getPrincipal() instanceof UsuarioPrincipal principal) {
            var usuarioAtual = usuarioRepository.findByIdComPessoa(principal.id()).orElse(null);

            if (usuarioAtual == null || !Boolean.TRUE.equals(usuarioAtual.getAtivo())) {
                SecurityContextHolder.clearContext();
                if (request.getSession(false) != null) {
                    request.getSession(false).invalidate();
                }
                escreverNaoAutorizado(response);
                return;
            }

            UsuarioPrincipal principalAtualizado = UsuarioPrincipal.de(usuarioAtual);
            if (!principalAtualizado.equals(principal)) {
                var novaAutenticacao = UsernamePasswordAuthenticationToken.authenticated(
                        principalAtualizado,
                        null,
                        principalAtualizado.getAuthorities()
                );
                novaAutenticacao.setDetails(autenticacao.getDetails());
                SecurityContextHolder.getContext().setAuthentication(novaAutenticacao);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void escreverNaoAutorizado(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":401,\"message\":\"Sua sessão não é mais válida.\"}");
    }
}

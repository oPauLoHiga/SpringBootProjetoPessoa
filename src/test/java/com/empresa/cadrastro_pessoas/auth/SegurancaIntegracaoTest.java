package com.empresa.cadrastro_pessoas.auth;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import com.empresa.cadrastro_pessoas.pessoa.repository.PessoaRepository;
import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.repository.TipoAcessoRepository;
import com.empresa.cadrastro_pessoas.usuario.Perfil;
import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import com.empresa.cadrastro_pessoas.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private TipoAcessoRepository tipoAcessoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveExigirLoginParaListarPessoas() throws Exception {
        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void devePermitirOsDoisEnderecosLocaisDoFrontend() throws Exception {
        for (String origem : new String[]{"http://localhost:5173", "http://127.0.0.1:5173"}) {
            mockMvc.perform(options("/api/pessoas")
                            .header(HttpHeaders.ORIGIN, origem)
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                    .andExpect(status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                            .string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origem));
        }
    }

    @Test
    void deveRestringirPessoasConformeOPerfil() throws Exception {
        mockMvc.perform(get("/api/pessoas")
                        .with(user("visitante@teste.com").roles("VISITANTE")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/pessoas")
                        .with(user("operador@teste.com").roles("OPERADOR")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/pessoas/999")
                        .with(user("operador@teste.com").roles("OPERADOR"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/tipos-acesso")
                        .with(user("operador@teste.com").roles("OPERADOR")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/tipos-acesso/ativos")
                        .with(user("operador@teste.com").roles("OPERADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void deveExigirCsrfEmOperacoesDeAlteracao() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .with(user("operador@teste.com").roles("OPERADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cadastroPublicoDeveCriarPessoaEVisitanteComSenhaProtegida() throws Exception {
        String json = """
                {
                  "nome": "Maria Visitante",
                  "cpf": "123.456.789-00",
                  "email": "MARIA@EXEMPLO.COM",
                  "telefone": "(11) 99999-8888",
                  "dataNascimento": "1995-05-10",
                  "endereco": "Rua das Flores, 10",
                  "cidade": "São Paulo",
                  "estado": "sp",
                  "senha": "SenhaSegura123",
                  "confirmacaoSenha": "SenhaSegura123"
                }
                """;

        mockMvc.perform(post("/api/auth/cadastro")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("VISITANTE"))
                .andExpect(jsonPath("$.email").value("maria@exemplo.com"));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase("maria@exemplo.com").orElseThrow();
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.VISITANTE);
        assertThat(usuario.getPessoa()).isNotNull();
        assertThat(usuario.getSenhaHash()).isNotEqualTo("SenhaSegura123");
        assertThat(passwordEncoder.matches("SenhaSegura123", usuario.getSenhaHash())).isTrue();
    }

    @Test
    void cadastroPublicoNaoDeveAceitarNomeCurtoDisfarcadoComEspacos() throws Exception {
        String json = """
                {
                  "nome": " A ",
                  "cpf": "555.666.777-88",
                  "email": "nome.curto@exemplo.com",
                  "dataNascimento": "1995-05-10",
                  "senha": "SenhaSegura123",
                  "confirmacaoSenha": "SenhaSegura123"
                }
                """;

        mockMvc.perform(post("/api/auth/cadastro")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nome deve ter pelo menos 2 caracteres."));

        assertThat(usuarioRepository.findByEmailIgnoreCase("nome.curto@exemplo.com")).isEmpty();
    }

    @Test
    void deveAutenticarPorSessaoERetornarUsuarioAtual() throws Exception {
        Usuario usuario = salvarUsuario("operador@exemplo.com", "SenhaSegura123", Perfil.OPERADOR, null);

        var resultadoLogin = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .param("email", usuario.getEmail())
                        .param("senha", "SenhaSegura123"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpSession sessao = (MockHttpSession) resultadoLogin.getRequest().getSession(false);
        assertThat(sessao).isNotNull();

        mockMvc.perform(get("/api/auth/me").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getId()))
                .andExpect(jsonPath("$.perfil").value("OPERADOR"));
    }

    @Test
    void mudancaDePerfilDeveValerNaSessaoExistente() throws Exception {
        Usuario usuario = salvarUsuario("equipe@exemplo.com", "SenhaSegura123", Perfil.OPERADOR, null);
        MockHttpSession sessao = autenticar(usuario.getEmail(), "SenhaSegura123");

        mockMvc.perform(get("/api/usuarios").session(sessao))
                .andExpect(status().isForbidden());

        usuario.setPerfil(Perfil.ADMIN);
        usuarioRepository.saveAndFlush(usuario);

        mockMvc.perform(get("/api/usuarios").session(sessao))
                .andExpect(status().isOk());
    }

    @Test
    void contaDesativadaDevePerderAcessoNaSessaoExistente() throws Exception {
        Usuario usuario = salvarUsuario("desativado@exemplo.com", "SenhaSegura123", Perfil.OPERADOR, null);
        MockHttpSession sessao = autenticar(usuario.getEmail(), "SenhaSegura123");

        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);

        mockMvc.perform(get("/api/pessoas").session(sessao))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sua sessão não é mais válida."));
    }

    @Test
    void pessoaDesativadaDevePerderAcessoNaSessaoExistente() throws Exception {
        Pessoa pessoa = salvarPessoa("Pessoa Operadora", "111.222.333-44", "pessoa.operadora@exemplo.com");
        Usuario usuario = salvarUsuario(pessoa.getEmail(), "SenhaSegura123", Perfil.OPERADOR, pessoa);
        MockHttpSession sessao = autenticar(usuario.getEmail(), "SenhaSegura123");

        pessoa.setAtivo(false);
        pessoaRepository.saveAndFlush(pessoa);

        mockMvc.perform(get("/api/pessoas").session(sessao))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sua sessão não é mais válida."));
    }

    @Test
    void pessoaDesativadaNaoDeveConseguirEntrar() throws Exception {
        Pessoa pessoa = salvarPessoa("Pessoa Inativa", "222.333.444-55", "pessoa.inativa@exemplo.com");
        pessoa.setAtivo(false);
        pessoaRepository.saveAndFlush(pessoa);
        salvarUsuario(pessoa.getEmail(), "SenhaSegura123", Perfil.VISITANTE, pessoa);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .param("email", pessoa.getEmail())
                        .param("senha", "SenhaSegura123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void redefinicaoDeSenhaDeveInvalidarSessaoExistente() throws Exception {
        Usuario usuario = salvarUsuario("senha@exemplo.com", "SenhaSegura123", Perfil.OPERADOR, null);
        MockHttpSession sessao = autenticar(usuario.getEmail(), "SenhaSegura123");

        usuario.setSenhaHash(passwordEncoder.encode("NovaSenhaSegura123"));
        usuarioRepository.saveAndFlush(usuario);

        mockMvc.perform(get("/api/pessoas").session(sessao))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sua sessão não é mais válida."));
    }

    @Test
    void requisicoesMalformadasDevemRetornarBadRequest() throws Exception {
        mockMvc.perform(get("/api/sugestoes/por-status")
                        .param("status", "DESCONHECIDO")
                        .with(user("admin@teste.com").roles("ADMIN")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(post("/api/sugestoes")
                        .with(user("admin@teste.com").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void visitanteDeveCriarSugestaoSomenteParaPessoaVinculada() throws Exception {
        TipoAcesso tipo = tipoAcessoRepository.findByNomeIgnoreCase("Visitante").orElseThrow();
        Pessoa pessoa = pessoaRepository.save(Pessoa.builder()
                .nome("João Visitante")
                .cpf("987.654.321-00")
                .email("joao@exemplo.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .ativo(true)
                .tipoAcesso(tipo)
                .build());
        salvarUsuario("joao@exemplo.com", "SenhaSegura123", Perfil.VISITANTE, pessoa);

        var resultadoLogin = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .param("email", "joao@exemplo.com")
                        .param("senha", "SenhaSegura123"))
                .andExpect(status().isNoContent())
                .andReturn();
        MockHttpSession sessao = (MockHttpSession) resultadoLogin.getRequest().getSession(false);

        mockMvc.perform(post("/api/minha-conta/sugestoes")
                        .session(sessao)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Melhoria no sistema",
                                  "descricao": "Gostaria de uma área de notificações."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pessoaId").value(pessoa.getId()))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    private Usuario salvarUsuario(String email, String senha, Perfil perfil, Pessoa pessoa) {
        return usuarioRepository.save(Usuario.builder()
                .email(email)
                .senhaHash(passwordEncoder.encode(senha))
                .perfil(perfil)
                .ativo(true)
                .pessoa(pessoa)
                .build());
    }

    private Pessoa salvarPessoa(String nome, String cpf, String email) {
        TipoAcesso tipo = tipoAcessoRepository.findByNomeIgnoreCase("Visitante").orElseThrow();
        return pessoaRepository.save(Pessoa.builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .ativo(true)
                .tipoAcesso(tipo)
                .build());
    }

    private MockHttpSession autenticar(String email, String senha) throws Exception {
        var resultadoLogin = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .param("email", email)
                        .param("senha", senha))
                .andExpect(status().isNoContent())
                .andReturn();

        return (MockHttpSession) resultadoLogin.getRequest().getSession(false);
    }
}

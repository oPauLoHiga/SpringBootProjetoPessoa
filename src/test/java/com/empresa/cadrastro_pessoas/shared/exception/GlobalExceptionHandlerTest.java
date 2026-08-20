package com.empresa.cadrastro_pessoas.shared.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarNotFoundParaRecursoInexistente() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(
                new ResourceNotFoundException("Pessoa não encontrada")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .containsEntry("status", 404)
                .containsEntry("message", "Pessoa não encontrada");
    }

    @Test
    void deveRetornarBadRequestParaRegraDeNegocio() {
        ResponseEntity<Map<String, Object>> response = handler.handleBusiness(
                new BusinessException("E-mail já cadastrado")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("status", 400)
                .containsEntry("message", "E-mail já cadastrado");
    }

    @Test
    void naoDeveExporDetalheDoErroInterno() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(
                new IllegalStateException("detalhe sensível")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .containsEntry("status", 500)
                .containsEntry("message", "Ocorreu um erro interno inesperado.");
        assertThat(response.getBody().toString()).doesNotContain("detalhe sensível");
    }

    @Test
    void naoDeveExporDetalheDoBancoEmConflitoDeDados() {
        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(
                new DataIntegrityViolationException("nome interno da restrição")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .containsEntry("status", 409)
                .containsEntry("message", "Já existe um cadastro com os dados únicos informados.");
        assertThat(response.getBody().toString()).doesNotContain("nome interno da restrição");
    }
}

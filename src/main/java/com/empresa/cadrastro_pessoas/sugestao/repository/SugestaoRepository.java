package com.empresa.cadrastro_pessoas.sugestao.repository;

import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import com.empresa.cadrastro_pessoas.sugestao.model.Sugestao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    // Todas as sugestões de uma pessoa específica
    List<Sugestao> findByPessoaId(Long pessoaId);

    // Filtrar sugestões por status
    List<Sugestao> findByStatus(StatusSugestao status);

    // Sugestões de uma pessoa com status específico
    List<Sugestao> findByPessoaIdAndStatus(Long pessoaId, StatusSugestao status);

    // Contar sugestões por status (útil para dashboard)
    long countByStatus(StatusSugestao status);
}

package com.empresa.cadrastro_pessoas.sugestao.repository;

import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import com.empresa.cadrastro_pessoas.sugestao.model.Sugestao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    @Override
    @EntityGraph(attributePaths = "pessoa")
    Optional<Sugestao> findById(Long id);

    // Todas as sugestões de uma pessoa específica
    @EntityGraph(attributePaths = "pessoa")
    List<Sugestao> findByPessoaIdOrderByDataCriacaoDesc(Long pessoaId);

    // Filtrar sugestões por status
    @EntityGraph(attributePaths = "pessoa")
    List<Sugestao> findByStatusOrderByDataCriacaoDesc(StatusSugestao status);

    @EntityGraph(attributePaths = "pessoa")
    List<Sugestao> findAllByOrderByDataCriacaoDesc();

    // Sugestões de uma pessoa com status específico
    List<Sugestao> findByPessoaIdAndStatus(Long pessoaId, StatusSugestao status);

    // Contar sugestões por status (útil para dashboard)
    long countByStatus(StatusSugestao status);

    long countByPessoaId(Long pessoaId);

    @Modifying(flushAutomatically = true)
    @Query("delete from Sugestao s where s.pessoa.id = :pessoaId")
    int deleteByPessoaId(@Param("pessoaId") Long pessoaId);
}

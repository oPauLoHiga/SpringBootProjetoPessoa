package com.empresa.cadrastro_pessoas.sugestao.repository;

import com.empresa.cadrastro_pessoas.sugestao.StatusSugestao;
import com.empresa.cadrastro_pessoas.sugestao.model.Sugestao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    // Todas as sugestões de uma pessoa específica
    List<Sugestao> findByPessoaIdOrderByDataCriacaoDesc(Long pessoaId);

    // Filtrar sugestões por status
    List<Sugestao> findByStatusOrderByDataCriacaoDesc(StatusSugestao status);

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

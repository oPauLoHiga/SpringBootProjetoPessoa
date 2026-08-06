package com.empresa.cadrastro_pessoas.tipoacesso.repository;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoAcessoRepository extends JpaRepository<TipoAcesso, Long> {

    // Busca por nome exato (case insensitive)
    Optional<TipoAcesso> findByNomeIgnoreCase(String nome);

    // Lista apenas os tipos ativos
    List<TipoAcesso> findByAtivoTrue();

    // Verifica se jÃ¡ existe tipo com o mesmo nome
    boolean existsByNomeIgnoreCase(String nome);
}
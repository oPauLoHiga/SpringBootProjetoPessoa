package com.empresa.cadrastro_pessoas.tipoacesso.repository;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoAcessoRepository extends JpaRepository<TipoAcesso, Long> {

    Optional<TipoAcesso> findByNomeIgnoreCase(String nome);

    List<TipoAcesso> findByAtivoTrue();

    boolean existsByNomeIgnoreCase(String nome);
}
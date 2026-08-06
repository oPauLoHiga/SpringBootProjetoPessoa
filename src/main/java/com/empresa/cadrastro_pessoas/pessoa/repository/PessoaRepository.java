package com.empresa.cadrastro_pessoas.pessoa.repository;

import com.empresa.cadrastro_pessoas.pessoa.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
        // Spring Data JPA gera a query automaticamente pelo nome do metodo!

        // Buscar por CPF
        Optional<Pessoa> findByCpf(String cpf);

        // Buscar por e-mail
        Optional<Pessoa> findByEmail(String email);

        // Verificar se CPF jÃ¡ existe
        boolean existsByCpf(String cpf);

        // Verificar se e-mail jÃ¡ existe
        boolean existsByEmail(String email);

        // Buscar todas as pessoas ativas
        List<Pessoa> findByAtivoTrue();

        List<Pessoa> findByTipoAcessoIsNull();

        // Buscar por nome (contendo, case-insensitive)
        List<Pessoa> findByNomeContainingIgnoreCase(String nome);

        // Buscar por cidade
        List<Pessoa> findByCidadeIgnoreCase(String cidade);

        // Query JPQL customizada: busca por nome ou e-mail
        @Query("SELECT p FROM Pessoa p WHERE " +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
        List<Pessoa> buscarPorNomeOuEmail(@Param("busca") String busca);
}

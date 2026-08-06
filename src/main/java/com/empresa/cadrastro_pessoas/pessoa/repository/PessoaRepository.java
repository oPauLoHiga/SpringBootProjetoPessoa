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

        Optional<Pessoa> findByCpf(String cpf);

        Optional<Pessoa> findByEmail(String email);

        boolean existsByCpf(String cpf);

        boolean existsByEmail(String email);

        List<Pessoa> findByAtivoTrue();

        List<Pessoa> findByTipoAcessoIsNull();

        List<Pessoa> findByNomeContainingIgnoreCase(String nome);

        List<Pessoa> findByCidadeIgnoreCase(String cidade);

        @Query("SELECT p FROM Pessoa p WHERE " +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
        List<Pessoa> buscarPorNomeOuEmail(@Param("busca") String busca);
}

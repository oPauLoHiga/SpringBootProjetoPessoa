package com.empresa.cadrastro_pessoas.tipoacesso.repository;

import com.empresa.cadrastro_pessoas.tipoacesso.TipoAcesso;
import com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoAcessoRepository extends JpaRepository<TipoAcesso, Long> {

    Optional<TipoAcesso> findByNomeIgnoreCase(String nome);

    @Query("""
            select new com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse(
                t.id, t.nome, t.descricao, t.ativo, count(p)
            )
            from TipoAcesso t
            left join t.pessoas p
            group by t.id, t.nome, t.descricao, t.ativo
            order by t.nome
            """)
    List<TipoAcessoResponse> listarTodosComTotalPessoas();

    @Query("""
            select new com.empresa.cadrastro_pessoas.tipoacesso.dto.TipoAcessoResponse(
                t.id, t.nome, t.descricao, t.ativo, count(p)
            )
            from TipoAcesso t
            left join t.pessoas p
            where t.ativo = true
            group by t.id, t.nome, t.descricao, t.ativo
            order by t.nome
            """)
    List<TipoAcessoResponse> listarAtivosComTotalPessoas();

    boolean existsByNomeIgnoreCase(String nome);
}

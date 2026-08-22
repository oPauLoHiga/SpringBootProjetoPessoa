package com.empresa.cadrastro_pessoas.usuario.repository;

import com.empresa.cadrastro_pessoas.usuario.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "pessoa")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "pessoa")
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findByIdComPessoa(@Param("id") Long id);

    @EntityGraph(attributePaths = "pessoa")
    Optional<Usuario> findByPessoaId(Long pessoaId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPessoaId(Long pessoaId);

    boolean existsByPerfil(com.empresa.cadrastro_pessoas.usuario.Perfil perfil);

    @Modifying(flushAutomatically = true)
    @Query("delete from Usuario u where u.pessoa.id = :pessoaId")
    int deleteByPessoaId(@Param("pessoaId") Long pessoaId);
}

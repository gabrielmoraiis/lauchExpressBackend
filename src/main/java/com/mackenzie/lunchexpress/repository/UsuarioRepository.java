package com.mackenzie.lunchexpress.repository;

import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByPerfil(PerfilUsuario perfil);
    List<Usuario> findByPerfilAndAtivo(PerfilUsuario perfil, Boolean ativo);
}

package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuarios no eliminados
    List<Usuario> findByEliminadoEnIsNull();

    // Buscar un usuario específico no eliminado
    Optional<Usuario> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar usuario por nombre de usuario no eliminado
    Optional<Usuario> findByNombreUsuarioAndEliminadoEnIsNull(String nombreUsuario);

    // Buscar usuario por nombre de usuario y contraseña no eliminado
    Optional<Usuario> findByNombreUsuarioAndContrasenaAndEliminadoEnIsNull(String nombreUsuario, String contrasena);

}

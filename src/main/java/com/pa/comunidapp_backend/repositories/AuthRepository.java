package com.pa.comunidapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Usuario;

@Repository
public interface AuthRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuarioAndEliminadoEnIsNull(String usuario);

    Optional<Usuario> findByNombreUsuarioAndContrasenaAndEliminadoEnIsNull(String usuario, String contrasena);
}

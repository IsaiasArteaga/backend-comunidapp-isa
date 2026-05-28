package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.UsuarioPermiso;

@Repository
public interface UsuarioPermisoRepository extends JpaRepository<UsuarioPermiso, Long> {

    // Buscar permisos de un usuario no eliminados
    List<UsuarioPermiso> findByUsuarioIdAndEliminadoEnIsNull(Long usuarioId);

    // Buscar un permiso específico de un usuario no eliminado
    Optional<UsuarioPermiso> findByUsuarioIdAndPermisoIdAndEliminadoEnIsNull(Long usuarioId, Long permisoId);

    // Buscar todos los permisos de un usuario (incluyendo eliminados)
    List<UsuarioPermiso> findByUsuarioId(Long usuarioId);
}

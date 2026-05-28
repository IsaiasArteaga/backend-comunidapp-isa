package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.AdminPermiso;

@Repository
public interface AdminPermisoRepository extends JpaRepository<AdminPermiso, Long> {

    // Buscar permisos de un usuario admin no eliminados
    List<AdminPermiso> findByUsuarioIdAndEliminadoEnIsNull(Long usuarioId);

    // Buscar un permiso específico de un usuario admin no eliminado
    Optional<AdminPermiso> findByUsuarioIdAndPermisoIdAndEliminadoEnIsNull(Long usuarioId, Long permisoId);

    // Buscar todos los permisos de un usuario admin (incluyendo eliminados)
    List<AdminPermiso> findByUsuarioId(Long usuarioId);
}

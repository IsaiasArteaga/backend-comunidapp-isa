package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    // Buscar permisos no eliminados
    List<Permiso> findByEliminadoEnIsNull();

    // Buscar un permiso específico no eliminado
    Optional<Permiso> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar permiso por código (no eliminado)
    Optional<Permiso> findByCodigoAndEliminadoEnIsNull(Long codigo);

    // Buscar permiso por código (incluyendo eliminados)
    Optional<Permiso> findByCodigo(Long codigo);
}

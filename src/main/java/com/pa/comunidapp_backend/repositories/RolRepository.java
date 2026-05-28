package com.pa.comunidapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    // Buscar rol por código
    Optional<Rol> findByCodigo(Long codigo);
}

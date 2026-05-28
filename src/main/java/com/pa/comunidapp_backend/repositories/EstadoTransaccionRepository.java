package com.pa.comunidapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.EstadoTransaccion;

@Repository
public interface EstadoTransaccionRepository extends JpaRepository<EstadoTransaccion, Integer> {
    Optional<EstadoTransaccion> findByCodigo(Integer codigo);
}

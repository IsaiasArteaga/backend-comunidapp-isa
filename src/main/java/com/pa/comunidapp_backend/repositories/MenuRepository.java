package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // Obtener menús por rol usando la entidad RolMenu
    @Query("SELECT rm.menu FROM RolMenu rm WHERE rm.rol.id = :rolId ORDER BY rm.menu.orden ASC")
    List<Menu> findMenusByRolId(@Param("rolId") Long rolId);

    // Buscar menús no eliminados
    List<Menu> findByEliminadoEnIsNull();

    // Buscar un menú específico no eliminado
    Optional<Menu> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar menú por ruta (no eliminado)
    Optional<Menu> findByRutaAndEliminadoEnIsNull(String ruta);
}

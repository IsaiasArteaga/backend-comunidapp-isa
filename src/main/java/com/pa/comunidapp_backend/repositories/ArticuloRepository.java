package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Articulo;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

        // Buscar todos los artículos no eliminados
        List<Articulo> findByEliminadoEnIsNull();

        // Buscar artículos no eliminados por usuario
        List<Articulo> findByUsuarioIdAndEliminadoEnIsNull(Long usuarioId);

        // Buscar un artículo específico no eliminado
        Optional<Articulo> findByIdAndEliminadoEnIsNull(Long id);

        // Buscar un artículo específico no eliminado por usuario
        Optional<Articulo> findByIdAndUsuarioIdAndEliminadoEnIsNull(Long id, Long usuarioId);

        // Búsqueda dinámica con múltiples criterios
        @Query("SELECT a FROM Articulo a JOIN Usuario u ON u.id = a.usuarioId WHERE a.eliminadoEn IS NULL " +
                        "AND (:nombreArticulo IS NULL OR LOWER(a.titulo) LIKE LOWER(CONCAT('%', :nombreArticulo, '%'))) "
                        +
                        "AND (:categoriaCodigo IS NULL OR a.categoriaCodigo = :categoriaCodigo) " +
                        "AND (:tipoTransaccionCodigo IS NULL OR a.tipoTransaccionCodigo = :tipoTransaccionCodigo) " +
                        "AND (:estadoArticuloCodigo IS NULL OR a.estadoArticuloCodigo = :estadoArticuloCodigo) " +
                        "AND (:nombreUsuario IS NULL OR LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :nombreUsuario, '%')))")
        List<Articulo> buscarArticulosConFiltros(
                        @Param("nombreArticulo") String nombreArticulo,
                        @Param("categoriaCodigo") Integer categoriaCodigo,
                        @Param("tipoTransaccionCodigo") Integer tipoTransaccionCodigo,
                        @Param("estadoArticuloCodigo") Integer estadoArticuloCodigo,
                        @Param("nombreUsuario") String nombreUsuario);

        // Búsqueda dinámica por CODIGO (para filtros desde el frontend)
        @Query("SELECT a FROM Articulo a " +
                        "JOIN Usuario u ON u.id = a.usuarioId " +
                        "WHERE a.eliminadoEn IS NULL " +
                        "AND (:nombreArticulo IS NULL OR LOWER(a.titulo) LIKE LOWER(CONCAT('%', :nombreArticulo, '%'))) " +
                        "AND (:categoriaCodigo IS NULL OR a.categoriaCodigo = :categoriaCodigo) " +
                        "AND (:tipoTransaccionCodigo IS NULL OR a.tipoTransaccionCodigo = :tipoTransaccionCodigo) " +
                        "AND (:estadoArticuloCodigo IS NULL OR a.estadoArticuloCodigo = :estadoArticuloCodigo) " +
                        "AND (:nombreUsuario IS NULL OR LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :nombreUsuario, '%')))")
        List<Articulo> buscarArticulosConFiltrosPorCodigo(
                        @Param("nombreArticulo") String nombreArticulo,
                        @Param("categoriaCodigo") Integer categoriaCodigo,
                        @Param("tipoTransaccionCodigo") Integer tipoTransaccionCodigo,
                        @Param("estadoArticuloCodigo") Integer estadoArticuloCodigo,
                        @Param("nombreUsuario") String nombreUsuario);

        // Buscar artículos por comercio no eliminados

        // Buscar artículos por comercio y categoría no eliminados
}

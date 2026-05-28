package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pa.comunidapp_backend.enums.EEstadoSolicitud;
import com.pa.comunidapp_backend.models.Articulo;
import com.pa.comunidapp_backend.models.Transaccion;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.repositories.ArticuloRepository;
import com.pa.comunidapp_backend.repositories.MisGestionesRepository;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;
import com.pa.comunidapp_backend.response.EstadisticasUsuarioDTO;

@Service
@Transactional
public class EstadisticasService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private MisGestionesRepository misGestionesRepository;

    public EstadisticasUsuarioDTO obtenerEstadisticas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findByIdAndEliminadoEnIsNull(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EstadisticasUsuarioDTO stats = new EstadisticasUsuarioDTO();

        // 1. Mis Recursos: artículos activos del usuario
        Long articulosActivos = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                .stream().count();
        stats.setMisRecursos(articulosActivos);

        // 2. Artículos publicados este mes
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long articulosMes = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                .stream()
                .filter(a -> a.getCreadoEn() != null && a.getCreadoEn().isAfter(inicioMes))
                .count();
        stats.setArticulosPublicadosMes(articulosMes);

        // 3. Transacciones del usuario (como propietario y solicitante)
        List<Transaccion> transaccionesPropietario = misGestionesRepository
                .findByUsuarioPropietarioIdAndEliminadoEnIsNull(usuarioId);
        List<Transaccion> transaccionesSolicitante = misGestionesRepository
                .findByUsuarioSolicitanteIdAndEliminadoEnIsNull(usuarioId);

        // 4. Intercambios totales
        long intercambiosTotales = transaccionesPropietario.size() + transaccionesSolicitante.size();
        stats.setIntercambios(intercambiosTotales);

        // 5. Intercambios completados (estado Devuelto)
        long intercambiosCompletados = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo()))
                .count();
        intercambiosCompletados += transaccionesSolicitante.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo()))
                .count();
        stats.setIntercambiosCompletados(intercambiosCompletados);

        // 6. Cancelaciones (rechazadas o canceladas)
        long cancelaciones = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Rechazada.getCodigo()) ||
                             t.getEstadoCodigo().equals(EEstadoSolicitud.Cancelado.getCodigo()))
                .count();
        cancelaciones += transaccionesSolicitante.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Rechazada.getCodigo()) ||
                             t.getEstadoCodigo().equals(EEstadoSolicitud.Cancelado.getCodigo()))
                .count();
        stats.setCancelaciones(cancelaciones);

        // 7. Tasa de aceptación
        double tasaAceptacion = intercambiosTotales > 0
                ? (intercambiosCompletados * 100.0 / intercambiosTotales)
                : 0;
        stats.setTasaAceptacion(tasaAceptacion);

        // 8. Usuarios contactados (usuarios únicos con transacciones)
        long usuariosContactados = transaccionesPropietario.stream()
                .map(Transaccion::getUsuarioSolicitanteId)
                .distinct()
                .count();
        usuariosContactados += transaccionesSolicitante.stream()
                .map(Transaccion::getUsuarioPropietarioId)
                .distinct()
                .count();
        stats.setUsuariosContactados(usuariosContactados);

        // 9. Reputación del usuario (rating promedio)
        stats.setMiReputacion(usuario.getRatingPromedio() != null ? usuario.getRatingPromedio().doubleValue() : 5.0);

        // 10. Gráfica de pastel: Distribución de estados de transacciones
        Map<String, Long> transaccionesPorEstado = new HashMap<>();
        long pendientes = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Pendiente.getCodigo())).count()
                + transaccionesSolicitante.stream()
                        .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Pendiente.getCodigo())).count();
        long aceptadas = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo())).count()
                + transaccionesSolicitante.stream()
                        .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo())).count();
        long devueltas = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo())).count()
                + transaccionesSolicitante.stream()
                        .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo())).count();
        long rechazadas = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Rechazada.getCodigo())).count()
                + transaccionesSolicitante.stream()
                        .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Rechazada.getCodigo())).count();
        long canceladas = transaccionesPropietario.stream()
                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Cancelado.getCodigo())).count()
                + transaccionesSolicitante.stream()
                        .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Cancelado.getCodigo())).count();

        transaccionesPorEstado.put("Pendiente", pendientes);
        transaccionesPorEstado.put("Aceptada", aceptadas);
        transaccionesPorEstado.put("Devuelto", devueltas);
        transaccionesPorEstado.put("Rechazada", rechazadas);
        transaccionesPorEstado.put("Cancelado", canceladas);
        stats.setTransaccionesPorEstado(transaccionesPorEstado);

        // 11. Gráfica de barras: Intercambios recibidos vs enviados
        Map<String, Long> intercambiosPorTipo = new HashMap<>();
        intercambiosPorTipo.put("Recibidos", (long) transaccionesPropietario.size());
        intercambiosPorTipo.put("Enviados", (long) transaccionesSolicitante.size());
        stats.setIntercambiosPorTipo(intercambiosPorTipo);

        // 12. Gráfica de pastel: Estados de mis artículos (Disponible vs Prestado)
        Map<String, Long> estadosArticulos = new HashMap<>();
        List<Articulo> misArticulos = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId);
        long disponibles = misArticulos.stream()
                .filter(a -> a.getEstadoArticuloCodigo() != null && a.getEstadoArticuloCodigo().equals(1))
                .count();
        long prestados = misArticulos.stream()
                .filter(a -> a.getEstadoArticuloCodigo() != null && a.getEstadoArticuloCodigo().equals(2))
                .count();
        estadosArticulos.put("Disponible", disponibles);
        estadosArticulos.put("Prestado", prestados);
        stats.setEstadosArticulos(estadosArticulos);

        return stats;
    }
}

package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pa.comunidapp_backend.dto.CalificacionCrearDTO;
import com.pa.comunidapp_backend.dto.SolicitudArticuloCrearDTO;
import com.pa.comunidapp_backend.enums.EEstadoArticulo;
import com.pa.comunidapp_backend.enums.EEstadoSolicitud;
import com.pa.comunidapp_backend.enums.ETipoSolicitud;
import com.pa.comunidapp_backend.models.Articulo;
import com.pa.comunidapp_backend.models.Calificacion;
import com.pa.comunidapp_backend.models.Transaccion;
import com.pa.comunidapp_backend.repositories.ArticuloRepository;
import com.pa.comunidapp_backend.repositories.CalificacionRepository;
import com.pa.comunidapp_backend.repositories.CategoriaRepository;
import com.pa.comunidapp_backend.repositories.CondicionArticuloRepository;
import com.pa.comunidapp_backend.repositories.EstadoArticuloRepository;
import com.pa.comunidapp_backend.repositories.EstadoTransaccionRepository;
import com.pa.comunidapp_backend.repositories.MisGestionesRepository;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;
import com.pa.comunidapp_backend.response.DetalleTransaccionResponseDTO;
import com.pa.comunidapp_backend.response.GestionUsuarioResponseDTO;
import com.pa.comunidapp_backend.response.MisGestionesConteoDTO;
import com.pa.comunidapp_backend.response.SolicitudResponseDTO;
import com.pa.comunidapp_backend.response.UsuarioBasicoDTO;

@Service
@Transactional
public class MisGestionesService {

    @Autowired
    private MisGestionesRepository misGestionesRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EstadoArticuloRepository estadoArticuloRepository;

    @Autowired
    private EstadoTransaccionRepository estadoTransaccionRepository;

    @Autowired
    private CondicionArticuloRepository condicionArticuloRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    public void crearSolicitud(SolicitudArticuloCrearDTO solicitudDTO, Long usuarioSolicitanteId) {
        // Verificar que el artículo existe
        Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(solicitudDTO.getArticuloId())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Verificar que no sea el propietario
        if (articulo.getUsuarioId().equals(usuarioSolicitanteId)) {
            throw new RuntimeException("No puedes solicitar tu propio artículo");
        }

        // Verificar que no exista una solicitud ACTIVA del mismo usuario
        // (Solo bloquea si está Pendiente, Aceptada o DevolucionPendiente)
        var solicitudActiva = misGestionesRepository
                .findByArticuloIdAndUsuarioSolicitanteIdAndEliminadoEnIsNull(
                        solicitudDTO.getArticuloId(), usuarioSolicitanteId)
                .filter(s -> s.getEstadoCodigo().equals(EEstadoSolicitud.Pendiente.getCodigo()) ||
                             s.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo()) ||
                             s.getEstadoCodigo().equals(EEstadoSolicitud.DevolucionPendiente.getCodigo()));

        if (solicitudActiva.isPresent()) {
            throw new RuntimeException("Ya existe una solicitud activa para este artículo");
        }

        Transaccion solicitud = new Transaccion();
        solicitud.setArticuloId(solicitudDTO.getArticuloId());
        solicitud.setUsuarioPropietarioId(articulo.getUsuarioId());
        solicitud.setUsuarioSolicitanteId(usuarioSolicitanteId);
        solicitud.setMensaje(solicitudDTO.getMensaje());
        solicitud.setEstadoCodigo(EEstadoSolicitud.Pendiente.getCodigo());
        solicitud.setTipoCodigo(ETipoSolicitud.Solicitud.getCodigo());
        solicitud.setCreadoEn(LocalDateTime.now());
        solicitud.setFechaEstimadaDevolucion(solicitudDTO.getFechaEstimadaDevolucion());

        misGestionesRepository.save(solicitud);
    }

    public void aceptarSolicitud(Long solicitudId, Long usuarioPropietarioId) {
        Transaccion solicitud = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Verificar que el usuario sea el propietario
        if (!solicitud.getUsuarioPropietarioId().equals(usuarioPropietarioId)) {
            throw new RuntimeException("No tienes permisos para aceptar esta solicitud");
        }

        // Cambiar estado del artículo a Prestado
        Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(solicitud.getArticuloId())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        articulo.setEstadoArticuloCodigo(EEstadoArticulo.Prestado.getCodigo());
        articuloRepository.save(articulo);

        // Actualizar solicitud
        solicitud.setEstadoCodigo(EEstadoSolicitud.Aceptada.getCodigo());
        solicitud.setTipoCodigo(ETipoSolicitud.Prestamo.getCodigo());
        solicitud.setRespondidoEn(LocalDateTime.now());
        misGestionesRepository.save(solicitud);

        // Actualizar solicitante en el artículo
        usuarioRepository.findById(solicitud.getUsuarioSolicitanteId()).ifPresent(usuario -> {
            try {
                // Aquí se usaría el ArticuloService para actualizar solicitante
                // Por ahora se mantiene la lógica simple
            } catch (Exception e) {
                // Manejar error
            }
        });
    }

    public void rechazarSolicitud(Long solicitudId, Long usuarioPropietarioId) {
        Transaccion solicitud = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Verificar que el usuario sea el propietario
        if (!solicitud.getUsuarioPropietarioId().equals(usuarioPropietarioId)) {
            throw new RuntimeException("No tienes permisos para rechazar esta solicitud");
        }

        solicitud.setEstadoCodigo(EEstadoSolicitud.Rechazada.getCodigo());
        solicitud.setRespondidoEn(LocalDateTime.now());
        misGestionesRepository.save(solicitud);
    }

    public void cancelarSolicitud(Long solicitudId, Long usuarioSolicitanteId) {
        Transaccion solicitud = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Verificar que el usuario sea el solicitante
        if (!solicitud.getUsuarioSolicitanteId().equals(usuarioSolicitanteId)) {
            throw new RuntimeException("No tienes permisos para cancelar esta solicitud");
        }

        solicitud.setEliminadoEn(LocalDateTime.now());
        misGestionesRepository.save(solicitud);
    }

    public SolicitudResponseDTO obtenerSolicitudPorId(Long solicitudId) {
        Transaccion solicitud = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return mapToResponseDTO(solicitud);
    }

    public DetalleTransaccionResponseDTO obtenerDetalleTransaccion(Long solicitudId) {
        Transaccion transaccion = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return mapToDetalleTransaccionDTO(transaccion);
    }

    private DetalleTransaccionResponseDTO mapToDetalleTransaccionDTO(Transaccion transaccion) {
        DetalleTransaccionResponseDTO dto = new DetalleTransaccionResponseDTO();
        dto.setId(transaccion.getId());
        dto.setPropietarioId(transaccion.getUsuarioPropietarioId());

        // Artículo con TODAS las imágenes y condición
        articuloRepository.findById(transaccion.getArticuloId()).ifPresent(articulo -> {
            dto.setNombreArticulo(articulo.getTitulo());
            // Obtener todas las imágenes
            if (articulo.getImagenes() != null && !articulo.getImagenes().isEmpty()) {
                String[] imagenesArray = articulo.getImagenes().split(",");
                List<String> imagenesList = java.util.Arrays.stream(imagenesArray)
                    .map(String::trim)
                    .collect(Collectors.toList());
                dto.setImagenes(imagenesList);
            }
            dto.setPrecio(articulo.getPrecio());
            // Categoría
            if (articulo.getCategoriaCodigo() != null) {
                categoriaRepository.findByCodigo(articulo.getCategoriaCodigo())
                    .ifPresent(cat -> dto.setCategoriaNombre(cat.getNombre()));
            }
            // Condición del artículo
            if (articulo.getCondicionCodigo() != null) {
                condicionArticuloRepository.findByCodigo(articulo.getCondicionCodigo())
                    .ifPresent(condicion -> {
                        dto.setCondicionCodigo(articulo.getCondicionCodigo());
                        dto.setCondicionNombre(condicion.getNombre());
                    });
            }
        });

        // Tipo de transacción (Solicitud o Préstamo)
        dto.setTipoCodigo(transaccion.getTipoCodigo());
        if (transaccion.getTipoCodigo() == 1) {
            dto.setTipoNombre("Solicitud");
        } else if (transaccion.getTipoCodigo() == 2) {
            dto.setTipoNombre("Préstamo");
        }

        // Estado de la transacción (Pendiente, Aceptada, Rechazada)
        dto.setEstadoCodigo(transaccion.getEstadoCodigo());
        estadoTransaccionRepository.findByCodigo(transaccion.getEstadoCodigo())
            .ifPresent(est -> dto.setEstadoNombre(est.getNombre()));

        dto.setFechaSolicitud(transaccion.getCreadoEn());
        dto.setMensaje(transaccion.getMensaje());
        dto.setMensajeRespuesta(transaccion.getMensajeRespuesta());
        dto.setFechaEstimadaDevolucion(transaccion.getFechaEstimadaDevolucion());

        // Solicitante
        UsuarioBasicoDTO solicitanteDTO = new UsuarioBasicoDTO();
        usuarioRepository.findById(transaccion.getUsuarioSolicitanteId()).ifPresent(usuario -> {
            solicitanteDTO.setId(usuario.getId());
            solicitanteDTO.setNombre(usuario.getNombreCompleto());
            solicitanteDTO.setEmail(usuario.getEmail());
            solicitanteDTO.setTelefono(usuario.getTelefono());
            solicitanteDTO.setDireccion(usuario.getDireccion());
            solicitanteDTO.setFoto(usuario.getAvatarUrl());
        });
        dto.setSolicitante(solicitanteDTO);

        // Propietario
        UsuarioBasicoDTO propietarioDTO = new UsuarioBasicoDTO();
        usuarioRepository.findById(transaccion.getUsuarioPropietarioId()).ifPresent(usuario -> {
            propietarioDTO.setId(usuario.getId());
            propietarioDTO.setNombre(usuario.getNombreCompleto());
            propietarioDTO.setEmail(usuario.getEmail());
            propietarioDTO.setTelefono(usuario.getTelefono());
            propietarioDTO.setDireccion(usuario.getDireccion());
            propietarioDTO.setFoto(usuario.getAvatarUrl());
        });
        dto.setPropietario(propietarioDTO);

        return dto;
    }

    private GestionUsuarioResponseDTO mapToGestionUsuarioDTO(Transaccion transaccion) {
        GestionUsuarioResponseDTO dto = new GestionUsuarioResponseDTO();
        dto.setId(transaccion.getId());
        dto.setPropietarioId(transaccion.getUsuarioPropietarioId());

        // Artículo
        articuloRepository.findById(transaccion.getArticuloId()).ifPresent(articulo -> {
            dto.setNombreArticulo(articulo.getTitulo());
            // Obtener solo la primera imagen
            if (articulo.getImagenes() != null && !articulo.getImagenes().isEmpty()) {
                String[] imagenes = articulo.getImagenes().split(",");
                dto.setImagenArticulo(imagenes[0].trim());
            }
            dto.setPrecio(articulo.getPrecio());
            // Categoría
            if (articulo.getCategoriaCodigo() != null) {
                categoriaRepository.findByCodigo(articulo.getCategoriaCodigo())
                    .ifPresent(cat -> dto.setCategoriaNombre(cat.getNombre()));
            }
        });

        // Tipo de transacción (Solicitud o Préstamo)
        dto.setTipoCodigo(transaccion.getTipoCodigo());
        if (transaccion.getTipoCodigo() == 1) {
            dto.setTipoNombre("Solicitud");
        } else if (transaccion.getTipoCodigo() == 2) {
            dto.setTipoNombre("Préstamo");
        }

        // Estado de la transacción (Pendiente, Aceptada, Rechazada)
        dto.setEstadoCodigo(transaccion.getEstadoCodigo());
        estadoTransaccionRepository.findByCodigo(transaccion.getEstadoCodigo())
            .ifPresent(est -> dto.setEstadoNombre(est.getNombre()));

        dto.setFechaSolicitud(transaccion.getCreadoEn());
        dto.setMensaje(transaccion.getMensaje());
        dto.setMensajeRespuesta(transaccion.getMensajeRespuesta());
        dto.setFechaEstimadaDevolucion(transaccion.getFechaEstimadaDevolucion());

        // Solicitante
        UsuarioBasicoDTO solicitanteDTO = new UsuarioBasicoDTO();
        usuarioRepository.findById(transaccion.getUsuarioSolicitanteId()).ifPresent(usuario -> {
            solicitanteDTO.setId(usuario.getId());
            solicitanteDTO.setNombre(usuario.getNombreCompleto());
            solicitanteDTO.setEmail(usuario.getEmail());
            solicitanteDTO.setTelefono(usuario.getTelefono());
            solicitanteDTO.setDireccion(usuario.getDireccion());
            solicitanteDTO.setFoto(usuario.getAvatarUrl());
        });
        dto.setSolicitante(solicitanteDTO);

        return dto;
    }

    public void cambiarEstadoSolicitud(Long solicitudId, Integer estadoCodigo, String mensajeRespuesta) {
        // Obtener la solicitud
        Transaccion solicitud = misGestionesRepository.findByIdAndEliminadoEnIsNull(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Obtener el artículo desde la solicitud
        Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(solicitud.getArticuloId())
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        // Cambiar estado de la solicitud
        solicitud.setEstadoCodigo(estadoCodigo);
        solicitud.setRespondidoEn(LocalDateTime.now());

        // Agregar mensaje de respuesta si se proporciona
        if (mensajeRespuesta != null && !mensajeRespuesta.trim().isEmpty()) {
            solicitud.setMensajeRespuesta(mensajeRespuesta);
        }

        // Si se acepta, cambiar el tipo a PRESTAMO y el estado del artículo a PRESTADO
        if (estadoCodigo == EEstadoSolicitud.Aceptada.getCodigo()) {
            solicitud.setTipoCodigo(ETipoSolicitud.Prestamo.getCodigo());
            articulo.setEstadoArticuloCodigo(EEstadoArticulo.Prestado.getCodigo());
            articuloRepository.save(articulo);
        }

        misGestionesRepository.save(solicitud);
    }

    private SolicitudResponseDTO mapToResponseDTO(Transaccion solicitud) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(solicitud.getId());
        dto.setArticuloId(solicitud.getArticuloId());
        dto.setMensaje(solicitud.getMensaje());
        dto.setEstadoCodigo(solicitud.getEstadoCodigo());
        dto.setTipoCodigo(solicitud.getTipoCodigo());
        dto.setCreadoEn(solicitud.getCreadoEn());
        dto.setRespondidoEn(solicitud.getRespondidoEn());

        // Obtener datos del artículo
        articuloRepository.findById(solicitud.getArticuloId()).ifPresent(articulo -> {
            dto.setArticuloTitulo(articulo.getTitulo());
        });

        // Obtener datos del solicitante
        usuarioRepository.findById(solicitud.getUsuarioSolicitanteId()).ifPresent(usuario -> {
            UsuarioBasicoDTO solicitanteDTO = new UsuarioBasicoDTO();
            solicitanteDTO.setId(usuario.getId());
            solicitanteDTO.setNombre(usuario.getNombreCompleto());
            solicitanteDTO.setEmail(usuario.getEmail());
            solicitanteDTO.setTelefono(usuario.getTelefono());
            solicitanteDTO.setDireccion(usuario.getDireccion());
            dto.setSolicitante(solicitanteDTO);
        });

        // Obtener datos del propietario
        usuarioRepository.findById(solicitud.getUsuarioPropietarioId()).ifPresent(usuario -> {
            UsuarioBasicoDTO propietarioDTO = new UsuarioBasicoDTO();
            propietarioDTO.setId(usuario.getId());
            propietarioDTO.setNombre(usuario.getNombreCompleto());
            propietarioDTO.setEmail(usuario.getEmail());
            propietarioDTO.setTelefono(usuario.getTelefono());
            propietarioDTO.setDireccion(usuario.getDireccion());
            dto.setPropietario(propietarioDTO);
        });

        return dto;
    }

    public MisGestionesConteoDTO obtenerConteos(Long usuarioId) {
        // Solicitudes enviadas PENDIENTES (tipo = 1, usuario solicitante, estado = Pendiente)
        long solicitudesEnviadas = misGestionesRepository
                .findByUsuarioSolicitanteIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Solicitud.getCodigo())
                .stream()
                .filter(s -> s.getEstadoCodigo().equals(EEstadoSolicitud.Pendiente.getCodigo()))
                .count();

        // Solicitudes recibidas PENDIENTES (tipo = 1, usuario propietario, estado = Pendiente)
        long solicitudesRecibidas = misGestionesRepository
                .findByUsuarioPropietarioIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Solicitud.getCodigo())
                .stream()
                .filter(s -> s.getEstadoCodigo().equals(EEstadoSolicitud.Pendiente.getCodigo()))
                .count();

        // Préstamos otorgados (tipo = 2, usuario propietario, estado = Aceptada o DevolucionPendiente)
        long prestamosOtorgados = misGestionesRepository
                .findByUsuarioPropietarioIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Prestamo.getCodigo())
                .stream()
                .filter(s -> s.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo()) ||
                             s.getEstadoCodigo().equals(EEstadoSolicitud.DevolucionPendiente.getCodigo()))
                .count();

        // Préstamos activos (tipo = 2, usuario solicitante, estado = Aceptada o DevolucionPendiente)
        long prestamosActivos = misGestionesRepository
                .findByUsuarioSolicitanteIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Prestamo.getCodigo())
                .stream()
                .filter(s -> s.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo()) ||
                             s.getEstadoCodigo().equals(EEstadoSolicitud.DevolucionPendiente.getCodigo()))
                .count();

        return new MisGestionesConteoDTO(solicitudesEnviadas, solicitudesRecibidas, prestamosOtorgados, prestamosActivos);
    }

    private int getEstadoOrden(Integer estadoCodigo) {
        switch (estadoCodigo) {
            case 1: return 0; // Pendiente
            case 2: return 1; // Aceptada
            case 4: return 2; // DevolucionPendiente
            case 5: return 3; // Devuelto
            case 3: return 4; // Rechazada
            case 6: return 5; // Cancelado
            default: return 6;
        }
    }

    public List<GestionUsuarioResponseDTO> obtenerSolicitudesEnviadas(Long usuarioId) {
        List<Transaccion> solicitudes = misGestionesRepository
                .findByUsuarioSolicitanteIdAndEliminadoEnIsNull(usuarioId);
        return solicitudes.stream()
                .filter(s -> s.getTipoCodigo().equals(ETipoSolicitud.Solicitud.getCodigo()) ||
                             (s.getTipoCodigo().equals(ETipoSolicitud.Prestamo.getCodigo()) && s.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo())))
                .sorted((t1, t2) -> {
                    int ordenEstado = Integer.compare(getEstadoOrden(t1.getEstadoCodigo()), getEstadoOrden(t2.getEstadoCodigo()));
                    if (ordenEstado != 0) return ordenEstado;
                    return t2.getCreadoEn().compareTo(t1.getCreadoEn()); // Más reciente primero
                })
                .map(this::mapToGestionUsuarioDTO).collect(Collectors.toList());
    }

    public List<GestionUsuarioResponseDTO> obtenerSolicitudesRecibidas(Long usuarioId) {
        List<Transaccion> solicitudes = misGestionesRepository
                .findByUsuarioPropietarioIdAndEliminadoEnIsNull(usuarioId);
        return solicitudes.stream()
                .filter(s -> s.getTipoCodigo().equals(ETipoSolicitud.Solicitud.getCodigo()) ||
                             (s.getTipoCodigo().equals(ETipoSolicitud.Prestamo.getCodigo()) && s.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo())))
                .sorted((t1, t2) -> {
                    int ordenEstado = Integer.compare(getEstadoOrden(t1.getEstadoCodigo()), getEstadoOrden(t2.getEstadoCodigo()));
                    if (ordenEstado != 0) return ordenEstado;
                    return t2.getCreadoEn().compareTo(t1.getCreadoEn()); // Más reciente primero
                })
                .map(this::mapToGestionUsuarioDTO).collect(Collectors.toList());
    }

    public List<GestionUsuarioResponseDTO> obtenerPrestamosOtorgados(Long usuarioId) {
        // Préstamos otorgados: yo soy el propietario (he prestado mis artículos)
        List<Transaccion> prestamos = misGestionesRepository
                .findByUsuarioPropietarioIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Prestamo.getCodigo());
        return prestamos.stream()
                .sorted((t1, t2) -> {
                    int ordenEstado = Integer.compare(getEstadoOrden(t1.getEstadoCodigo()), getEstadoOrden(t2.getEstadoCodigo()));
                    if (ordenEstado != 0) return ordenEstado;
                    return t2.getCreadoEn().compareTo(t1.getCreadoEn()); // Más reciente primero
                })
                .map(this::mapToGestionUsuarioDTO).collect(Collectors.toList());
    }

    public List<GestionUsuarioResponseDTO> obtenerPrestamosActivos(Long usuarioId) {
        // Préstamos activos: yo soy el solicitante (me han prestado)
        List<Transaccion> prestamos = misGestionesRepository
                .findByUsuarioSolicitanteIdAndTipoCodigoAndEliminadoEnIsNull(usuarioId, ETipoSolicitud.Prestamo.getCodigo());
        return prestamos.stream()
                .sorted((t1, t2) -> {
                    int ordenEstado = Integer.compare(getEstadoOrden(t1.getEstadoCodigo()), getEstadoOrden(t2.getEstadoCodigo()));
                    if (ordenEstado != 0) return ordenEstado;
                    return t2.getCreadoEn().compareTo(t1.getCreadoEn()); // Más reciente primero
                })
                .map(this::mapToGestionUsuarioDTO).collect(Collectors.toList());
    }

    private void registrarCalificacion(Long transaccionId, Long usuarioQueCalificaId, CalificacionCrearDTO calificacionDTO) {
        // Obtener la transacción
        Transaccion transaccion = misGestionesRepository.findByIdAndEliminadoEnIsNull(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        // Determinar quién es el usuario calificado (el otro usuario)
        Long usuarioCalificadoId;
        if (usuarioQueCalificaId.equals(transaccion.getUsuarioSolicitanteId())) {
            usuarioCalificadoId = transaccion.getUsuarioPropietarioId();
        } else if (usuarioQueCalificaId.equals(transaccion.getUsuarioPropietarioId())) {
            usuarioCalificadoId = transaccion.getUsuarioSolicitanteId();
        } else {
            throw new RuntimeException("El usuario no está autorizado a calificar esta transacción");
        }

        // Crear y guardar la calificación
        Calificacion calificacion = new Calificacion();
        calificacion.setTransaccionId(transaccionId);
        calificacion.setUsuarioQueCalificaId(usuarioQueCalificaId);
        calificacion.setUsuarioCalificadoId(usuarioCalificadoId);
        calificacion.setPuntuacion(calificacionDTO.getPuntuacion());
        calificacion.setComentario(calificacionDTO.getComentario());
        calificacion.setCreadoEn(LocalDateTime.now());
        calificacionRepository.save(calificacion);

        // Actualizar el rating promedio del usuario calificado
        actualizarRatingPromedio(usuarioCalificadoId);
    }

    private void actualizarRatingPromedio(Long usuarioId) {
        // Obtener todas las calificaciones del usuario
        List<Calificacion> calificaciones = calificacionRepository.findByUsuarioCalificadoIdAndEliminadoEnIsNull(usuarioId);

        if (!calificaciones.isEmpty()) {
            // Calcular el promedio
            double promedio = calificaciones.stream()
                    .mapToDouble(Calificacion::getPuntuacion)
                    .average()
                    .orElse(5.0);

            // Actualizar el usuario
            usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
                usuario.setRatingPromedio((float) promedio);
                usuarioRepository.save(usuario);
            });
        }
    }

    public void confirmarDevolucion(Long transaccionId, Long usuarioQueCalificaId, CalificacionCrearDTO calificacionDTO) {
        Transaccion transaccion = misGestionesRepository.findByIdAndEliminadoEnIsNull(transaccionId)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        // Solo se puede confirmar si está en DevolucionPendiente
        if (!transaccion.getEstadoCodigo().equals(EEstadoSolicitud.DevolucionPendiente.getCodigo())) {
            throw new RuntimeException("Solo se puede confirmar cuando está en DevolucionPendiente");
        }

        transaccion.setEstadoCodigo(EEstadoSolicitud.Devuelto.getCodigo());
        transaccion.setRespondidoEn(LocalDateTime.now());
        misGestionesRepository.save(transaccion);

        // Cambiar el estado del artículo de vuelta a Disponible
        articuloRepository.findById(transaccion.getArticuloId()).ifPresent(articulo -> {
            articulo.setEstadoArticuloCodigo(EEstadoArticulo.Disponible.getCodigo());
            articuloRepository.save(articulo);
        });

        // Registrar la calificación si se proporciona
        if (calificacionDTO != null && calificacionDTO.getPuntuacion() != null) {
            registrarCalificacion(transaccionId, usuarioQueCalificaId, calificacionDTO);
        }
    }
}

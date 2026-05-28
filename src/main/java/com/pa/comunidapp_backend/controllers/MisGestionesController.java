package com.pa.comunidapp_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.dto.ActualizarEstadoTransaccionDTO;
import com.pa.comunidapp_backend.dto.CalificacionCrearDTO;
import com.pa.comunidapp_backend.dto.SolicitudArticuloCrearDTO;
import com.pa.comunidapp_backend.response.DetalleTransaccionResponseDTO;
import com.pa.comunidapp_backend.response.GestionUsuarioResponseDTO;
import com.pa.comunidapp_backend.response.MisGestionesConteoDTO;
import com.pa.comunidapp_backend.services.MisGestionesService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mis-gestiones")
@Tag(name = "Mis Gestiones")
public class MisGestionesController {

    @Autowired
    private MisGestionesService solicitudService;

    @PostMapping("/solicitar")
    public ResponseEntity<Void> crearSolicitud(
            @Valid @RequestBody SolicitudArticuloCrearDTO solicitudDTO,
            @RequestParam Long usuarioId) {
        solicitudService.crearSolicitud(solicitudDTO, usuarioId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/estado/{solicitudId}")
    public ResponseEntity<Void> cambiarEstadoSolicitud(
            @PathVariable Long solicitudId,
            @Valid @RequestBody ActualizarEstadoTransaccionDTO actualizarDTO) {
        solicitudService.cambiarEstadoSolicitud(solicitudId, actualizarDTO.getEstadoCodigo(), actualizarDTO.getMensajeRespuesta());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{solicitudId}")
    public ResponseEntity<DetalleTransaccionResponseDTO> obtenerSolicitudPorId(
            @PathVariable Long solicitudId) {
        DetalleTransaccionResponseDTO solicitud = solicitudService.obtenerDetalleTransaccion(solicitudId);
        return new ResponseEntity<>(solicitud, HttpStatus.OK);
    }

    @GetMapping("/conteos/{usuarioId}")
    public ResponseEntity<MisGestionesConteoDTO> obtenerConteos(
            @PathVariable Long usuarioId) {
        MisGestionesConteoDTO conteos = solicitudService.obtenerConteos(usuarioId);
        return new ResponseEntity<>(conteos, HttpStatus.OK);
    }

    @GetMapping("/solicitudes-enviadas/{usuarioId}")
    public ResponseEntity<List<GestionUsuarioResponseDTO>> obtenerSolicitudesEnviadas(
            @PathVariable Long usuarioId) {
        List<GestionUsuarioResponseDTO> solicitudes = solicitudService.obtenerSolicitudesEnviadas(usuarioId);
        return new ResponseEntity<>(solicitudes, HttpStatus.OK);
    }

    @GetMapping("/solicitudes-recibidas/{usuarioId}")
    public ResponseEntity<List<GestionUsuarioResponseDTO>> obtenerSolicitudesRecibidas(
            @PathVariable Long usuarioId) {
        List<GestionUsuarioResponseDTO> solicitudes = solicitudService.obtenerSolicitudesRecibidas(usuarioId);
        return new ResponseEntity<>(solicitudes, HttpStatus.OK);
    }

    @GetMapping("/prestamos-otorgados/{usuarioId}")
    public ResponseEntity<List<GestionUsuarioResponseDTO>> obtenerPrestamosOtorgados(
            @PathVariable Long usuarioId) {
        List<GestionUsuarioResponseDTO> prestamos = solicitudService.obtenerPrestamosOtorgados(usuarioId);
        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }

    @GetMapping("/prestamos-activos/{usuarioId}")
    public ResponseEntity<List<GestionUsuarioResponseDTO>> obtenerPrestamosActivos(
            @PathVariable Long usuarioId) {
        List<GestionUsuarioResponseDTO> prestamos = solicitudService.obtenerPrestamosActivos(usuarioId);
        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }

    @PutMapping("/confirmar-devolucion/{transaccionId}")
    public ResponseEntity<Void> confirmarDevolucion(
            @PathVariable Long transaccionId,
            @RequestParam Long usuarioId,
            @Valid @RequestBody CalificacionCrearDTO calificacionDTO) {
        solicitudService.confirmarDevolucion(transaccionId, usuarioId, calificacionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


package com.pa.comunidapp_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.response.EstadisticasUsuarioDTO;
import com.pa.comunidapp_backend.services.EstadisticasService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/estadisticas")
@Tag(name = "Estadísticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<EstadisticasUsuarioDTO> obtenerEstadisticas(
            @PathVariable Long usuarioId) {
        EstadisticasUsuarioDTO estadisticas = estadisticasService.obtenerEstadisticas(usuarioId);
        return new ResponseEntity<>(estadisticas, HttpStatus.OK);
    }
}

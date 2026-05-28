package com.pa.comunidapp_backend.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasUsuarioDTO {
    // Números clave del usuario
    private Long misRecursos;              // Artículos activos del usuario
    private Long intercambios;             // Total de transacciones (propietario + solicitante)
    private Long intercambiosCompletados;  // Transacciones con estado Devuelto
    private Long cancelaciones;            // Transacciones rechazadas o canceladas
    private Long usuariosContactados;      // Usuarios únicos con los que ha transaccionado
    private Long articulosPublicadosMes;   // Artículos creados en este mes
    // Ratings y porcentajes
    private Double miReputacion;           // ratingPromedio del usuario
    private Double tasaAceptacion;         // (intercambiosCompletados / intercambios) * 100

    // Gráfica de pastel: Distribución de estados de transacciones
    private Map<String, Long> transaccionesPorEstado;  // Pendiente, Aceptada, Devuelto, Rechazada, Cancelado

    // Gráfica de barras: Intercambios recibidos vs enviados (como Map para gráficas)
    private Map<String, Long> intercambiosPorTipo;     // {Recibidos, Enviados}

    // Gráfica de pastel: Estados de mis artículos
    private Map<String, Long> estadosArticulos;       // {Disponible, Prestado}
}

package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeloInactividadUsuarioDTO {
    private String nombre;
    private String descripcion;
    private Long usuarioId;
    private String usuarioNombre;
    private String graficoTipo;
    private DatosGraficoActividadDTO datosGrafico;
}

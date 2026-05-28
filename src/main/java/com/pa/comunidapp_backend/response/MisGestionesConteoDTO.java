package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MisGestionesConteoDTO {
    private Long solicitudesEnviadas;
    private Long solicitudesRecibidas;
    private Long prestamosOtorgados;
    private Long prestamosActivos;
}

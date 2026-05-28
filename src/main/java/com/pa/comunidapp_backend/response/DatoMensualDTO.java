package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatoMensualDTO {
    private String mes;
    private Double tasaVenta;
}

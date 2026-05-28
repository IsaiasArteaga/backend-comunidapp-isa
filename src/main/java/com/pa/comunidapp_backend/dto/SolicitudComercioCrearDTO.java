package com.pa.comunidapp_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudComercioCrearDTO {

    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombreNegocio;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Size(max = 500, message = "La razón social no puede exceder 500 caracteres")
    private String razonSocial;

    private String telefonoContacto;

    @Email(message = "El email debe ser válido")
    private String emailContacto;
}

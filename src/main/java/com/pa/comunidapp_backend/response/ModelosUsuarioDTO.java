package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelosUsuarioDTO {
    private ModeloConfiabilidadUsuarioDTO modelo5ConfiabilidadUsuario;
    private ModeloInactividadUsuarioDTO modelo6InactividadUsuario;
}

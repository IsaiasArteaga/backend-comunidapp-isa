package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelosGlobalesDTO {
    private ModeloExitoVentaDTO modelo1ExitoVenta;
    private ModeloCumplimientoPrestamosDTO modelo2CumplimientoPrestamos;
    private ModeloTendenciaCategoriasDTO modelo3TendenciaCategorias;
    private ModeloDemandaCondicionesDTO modelo4DemandaCondiciones;
}

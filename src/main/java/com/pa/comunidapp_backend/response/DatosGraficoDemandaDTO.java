package com.pa.comunidapp_backend.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosGraficoDemandaDTO {
    private List<CondicionDemandaDTO> condiciones;
}

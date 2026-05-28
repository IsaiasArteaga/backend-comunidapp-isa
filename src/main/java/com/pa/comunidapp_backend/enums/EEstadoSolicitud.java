package com.pa.comunidapp_backend.enums;

public enum EEstadoSolicitud {
    Pendiente(1),
    Aceptada(2),
    Rechazada(3),
    DevolucionPendiente(4),
    Devuelto(5),
    Cancelado(6);

    private final int codigo;

    EEstadoSolicitud(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

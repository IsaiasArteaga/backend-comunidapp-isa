package com.pa.comunidapp_backend.enums;

public enum ETipoSolicitud {
    Solicitud(1),
    Prestamo(2);

    private final int codigo;

    ETipoSolicitud(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

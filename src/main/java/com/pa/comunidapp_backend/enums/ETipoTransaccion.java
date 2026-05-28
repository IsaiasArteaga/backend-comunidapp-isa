package com.pa.comunidapp_backend.enums;

public enum ETipoTransaccion {
    Venta(1),
    Prestamo(2);

    private final int codigo;

    ETipoTransaccion(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

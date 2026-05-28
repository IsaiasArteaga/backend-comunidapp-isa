package com.pa.comunidapp_backend.enums;

public enum EEstadoArticulo {
    Disponible(1),
    Prestado(2);

    private final int codigo;

    EEstadoArticulo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

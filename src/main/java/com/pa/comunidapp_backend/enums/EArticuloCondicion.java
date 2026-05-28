package com.pa.comunidapp_backend.enums;

public enum EArticuloCondicion {
    Nuevo(1),
    PocoUso(2),
    Usado(3),
    Dañado(4),
    Defectuoso(5);

    private final int codigo;

    EArticuloCondicion(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}

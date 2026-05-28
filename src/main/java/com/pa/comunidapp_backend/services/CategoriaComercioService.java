package com.pa.comunidapp_backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.models.CategoriaComercio;
import com.pa.comunidapp_backend.models.CategoriaComercioFiltro;
import com.pa.comunidapp_backend.repositories.CategoriaComercioRepository;

@Service
public class CategoriaComercioService {

    @Autowired
    private CategoriaComercioRepository categoriaComercioRepository;

    /**
     * Obtiene todas las categorías de comercios no eliminadas
     */
    public List<CategoriaComercioFiltro> obtenerTodasLasCategorias() {
        List<CategoriaComercio> categorias = categoriaComercioRepository.findByEliminadoEnIsNull();
        List<CategoriaComercioFiltro> categoriasFiltro = new ArrayList<>();

        for (CategoriaComercio categoria : categorias) {
            categoriasFiltro.add(new CategoriaComercioFiltro(categoria.getCodigo(), categoria.getNombre()));
        }

        return categoriasFiltro;
    }

    /**
     * Obtiene una categoría por código
     */
    public CategoriaComercioFiltro obtenerCategoriaPorCodigo(Integer codigo) {
        CategoriaComercio categoria = categoriaComercioRepository.findByCodigoAndEliminadoEnIsNull(codigo)
                .orElseThrow(() -> new RuntimeException("Categoría de comercio no encontrada"));
        return new CategoriaComercioFiltro(categoria.getCodigo(), categoria.getNombre());
    }

    /**
     * Obtiene una categoría por ID
     */
    public CategoriaComercioFiltro obtenerCategoriaPorId(Long id) {
        CategoriaComercio categoria = categoriaComercioRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Categoría de comercio no encontrada"));
        return new CategoriaComercioFiltro(categoria.getCodigo(), categoria.getNombre());
    }
}

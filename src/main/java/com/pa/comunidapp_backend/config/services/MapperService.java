package com.pa.comunidapp_backend.config.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperService {

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ImagenService imagenService;

    public <S, T> T map(S source, Class<T> targetClass) {
        T result = modelMapper.map(source, targetClass);

        // Manejar conversión automática de imágenes si es necesario
        if (necesitaConversionImagenes(source, targetClass)) {
            convertirImagenes(source, result);
        }

        return result;
    }

    private <S, T> boolean necesitaConversionImagenes(S source, Class<T> targetClass) {
        try {
            boolean sourceTieneImagenes = source.getClass().getDeclaredField("imagenes") != null;
            boolean targetTieneImagenes = targetClass.getDeclaredField("imagenes") != null;

            if (sourceTieneImagenes && targetTieneImagenes) {
                // Verificar tipos específicos
                String sourceImagenesType = source.getClass().getDeclaredField("imagenes").getType().getSimpleName();
                String targetImagenesType = targetClass.getDeclaredField("imagenes").getType().getSimpleName();

                // Conversión en ambas direcciones:
                // List<String> → String (DTO → Entity)
                // String → List<String> (Entity → ResponseDTO)
                return ("List".equals(sourceImagenesType) && "String".equals(targetImagenesType)) ||
                        ("String".equals(sourceImagenesType) && "List".equals(targetImagenesType));
            }
        } catch (NoSuchFieldException e) {
            // Si no tiene el campo imagenes, no necesita conversión
        }
        return false;
    }

    private <S, T> void convertirImagenes(S source, T target) {
        try {
            // Obtener el campo imagenes del source
            var imagenesField = source.getClass().getDeclaredField("imagenes");
            imagenesField.setAccessible(true);
            Object imagenesSource = imagenesField.get(source);

            // Obtener el campo imagenes del target
            var targetImagenesField = target.getClass().getDeclaredField("imagenes");
            targetImagenesField.setAccessible(true);

            // Determinar el tipo de conversión
            String sourceType = imagenesField.getType().getSimpleName();
            String targetType = targetImagenesField.getType().getSimpleName();

            if ("String".equals(sourceType) && "List".equals(targetType)) {
                // String → List<String> (Entity → ResponseDTO)
                String imagenesJson = (String) imagenesSource;
                List<String> imagenes = imagenService.jsonToImagenes(imagenesJson);
                targetImagenesField.set(target, imagenes);
            } else if ("List".equals(sourceType) && "String".equals(targetType)) {
                // List<String> → String (DTO → Entity)
                @SuppressWarnings("unchecked")
                List<String> imagenesList = (List<String>) imagenesSource;
                String imagenesJson = imagenService.imagenesToJson(imagenesList);
                targetImagenesField.set(target, imagenesJson);
            }

        } catch (Exception e) {
            // Si hay error, dejar el campo como null
            try {
                var targetImagenesField = target.getClass().getDeclaredField("imagenes");
                targetImagenesField.setAccessible(true);
                targetImagenesField.set(target, null);
            } catch (Exception ex) {
                // Ignorar error
            }
        }
    }

}

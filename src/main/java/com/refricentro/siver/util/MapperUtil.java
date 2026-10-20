package com.refricentro.siver.util;

import java.util.List;
import org.modelmapper.ModelMapper;

/**
 * Ayudas para convertir con ModelMapper.
 *
 * ModelMapper convierte un objeto a la vez. Casi todos los endpoints devuelven
 * listas, y escribir `.stream().map(x -> mapper.map(x, Dto.class)).toList()`
 * en cada uno se repite mucho: aqui queda escrito una sola vez.
 */
public final class MapperUtil {

    private MapperUtil() {
        // Solo metodos estaticos: no se crean instancias.
    }

    /**
     * Convierte cada elemento de la lista al tipo indicado.
     *
     *   MapperUtil.mapList(mapper, clientes, ClienteResponse.class)
     */
    public static <S, T> List<T> mapList(ModelMapper mapper, List<S> origen, Class<T> destino) {
        return origen.stream()
                .map(elemento -> mapper.map(elemento, destino))
                .toList();
    }
}

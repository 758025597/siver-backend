package com.refricentro.siver.dto;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * Una pagina de resultados, lista para mandar por la API.
 *
 * Por que no se devuelve el Page<T> de Spring tal cual: su JSON cambia entre
 * versiones de Spring Data (y Spring avisa que no es estable). Este record
 * deja fijo el formato que espera Angular:
 *
 *   {
 *     "content":       [ ...los registros de esta pagina... ],
 *     "number":        0,     pagina actual (la primera es la 0)
 *     "size":          10,    registros por pagina
 *     "totalElements": 57,    registros en TODA la tabla
 *     "totalPages":    6,
 *     "first":         true,
 *     "last":          false
 *   }
 *
 * Con totalElements el paginador de Angular sabe cuantas paginas dibujar.
 */
public record PaginaResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    /** Convierte un Page de entidades en una pagina de DTOs. */
    public static <E, T> PaginaResponse<T> de(Page<E> pagina, Function<E, T> convertir) {
        return new PaginaResponse<>(
                pagina.getContent().stream().map(convertir).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast());
    }
}

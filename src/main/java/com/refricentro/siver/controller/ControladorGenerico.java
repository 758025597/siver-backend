package com.refricentro.siver.controller;

import com.refricentro.siver.service.CrudService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Los 5 endpoints REST escritos UNA SOLA VEZ para todas las entidades.
 *
 * Trabaja con DTOs y no con entidades, para no exponer la base de datos
 * por la API. El caso claro es Usuario: la entidad tiene el campo `clave`,
 * y si se devolviera tal cual, la API estaria publicando las contrasenas.
 *
 * Tipos:
 *   T   = entidad        (Producto)
 *   ID  = tipo del id    (Integer)
 *   REQ = lo que ENTRA   (ProductoRequest)
 *   RES = lo que SALE    (ProductoResponse)
 *
 * Como se usa:
 *
 *   @RestController
 *   @RequestMapping("/api/roles")
 *   public class RolController
 *           extends ControladorGenerico<Rol, Integer, RolRequest, RolResponse> {
 *
 *       public RolController(RolService servicio) { super(servicio); }
 *
 *       protected Rol aEntidad(RolRequest req) { ... }
 *       protected RolResponse aRespuesta(Rol rol) { ... }
 *   }
 *
 * Cada controlador concreto solo pone su @RequestMapping y las dos
 * conversiones. Los 5 endpoints los recibe gratis por herencia.
 */
public abstract class ControladorGenerico<T, ID, REQ, RES> {

    protected final CrudService<T, ID> servicio;

    protected ControladorGenerico(CrudService<T, ID> servicio) {
        this.servicio = servicio;
    }

    /** Convierte lo que llega por la API en la entidad. */
    protected abstract T aEntidad(REQ request);

    /** Convierte la entidad en lo que se devuelve por la API. */
    protected abstract RES aRespuesta(T entidad);

    /** GET /api/xxx  ->  200 con la lista */
    @GetMapping
    public ResponseEntity<List<RES>> listar() {
        List<RES> respuesta = servicio.listar().stream()
                .map(this::aRespuesta)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/xxx/{id}  ->  200, o 404 si no existe */
    @GetMapping("/{id}")
    public ResponseEntity<RES> buscarPorId(@PathVariable ID id) {
        return ResponseEntity.ok(aRespuesta(servicio.buscarPorId(id)));
    }

    /** POST /api/xxx  ->  201 Created */
    @PostMapping
    public ResponseEntity<RES> crear(@Valid @RequestBody REQ request) {
        T creada = servicio.crear(aEntidad(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(creada));
    }

    /** PUT /api/xxx/{id}  ->  200 con el registro actualizado */
    @PutMapping("/{id}")
    public ResponseEntity<RES> actualizar(@PathVariable ID id, @Valid @RequestBody REQ request) {
        T actualizada = servicio.actualizar(id, aEntidad(request));
        return ResponseEntity.ok(aRespuesta(actualizada));
    }

    /** DELETE /api/xxx/{id}  ->  204 No Content (sin cuerpo) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable ID id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.refricentro.siver.controller;

import com.refricentro.siver.service.CrudService;
import com.refricentro.siver.util.MapperUtil;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
 * Nivel 2 de Richardson: cada operacion usa su verbo HTTP y su codigo:
 *   GET 200 · POST 201 + cabecera Location · PUT 200 · DELETE 204
 *
 * Como se usa:
 *
 *   @RestController
 *   @RequestMapping("/api/roles")
 *   @RequiredArgsConstructor
 *   public class RolController
 *           extends ControladorGenerico<Rol, Integer, RolRequest, RolResponse> {
 *
 *       private final RolService servicio;
 *       @Qualifier("rolMapper")
 *       private final ModelMapper mapper;
 *
 *       protected RolService getServicio() { return servicio; }
 *       protected ModelMapper getMapper() { return mapper; }
 *       protected Integer obtenerId(Rol rol) { return rol.getIdRol(); }
 *   }
 *
 * Las conversiones las hace ModelMapper. Un controlador solo sobrescribe
 * aEntidad() o aRespuesta() cuando necesita algo mas, por ejemplo buscar la
 * categoria de un producto a partir de su id.
 */
public abstract class ControladorGenerico<T, ID, REQ, RES> {

    private final Class<T> claseEntidad;
    private final Class<RES> claseRespuesta;

    /**
     * Averigua las clases reales de T y RES leyendo la declaracion del hijo
     * (por ejemplo `extends ControladorGenerico<Rol, Integer, RolRequest,
     * RolResponse>`). ModelMapper las necesita para saber a que convertir.
     */
    @SuppressWarnings("unchecked")
    protected ControladorGenerico() {
        Class<?>[] tipos = GenericTypeResolver.resolveTypeArguments(getClass(), ControladorGenerico.class);
        this.claseEntidad = (Class<T>) tipos[0];
        this.claseRespuesta = (Class<RES>) tipos[3];
    }

    /** El servicio de la entidad. */
    protected abstract CrudService<T, ID> getServicio();

    /** El ModelMapper que convierte esta entidad (defaultMapper o uno propio). */
    protected abstract ModelMapper getMapper();

    /** El id de la entidad recien creada, para armar la cabecera Location. */
    protected abstract ID obtenerId(T entidad);

    /** Convierte lo que llega por la API en la entidad. */
    protected T aEntidad(REQ request) {
        return getMapper().map(request, claseEntidad);
    }

    /** Convierte la entidad en lo que se devuelve por la API. */
    protected RES aRespuesta(T entidad) {
        return getMapper().map(entidad, claseRespuesta);
    }

    /** Convierte una lista completa, para los endpoints que devuelven varias. */
    protected List<RES> aRespuestas(List<T> entidades) {
        return MapperUtil.mapList(getMapper(), entidades, claseRespuesta);
    }

    /** GET /api/xxx  ->  200 con la lista */
    @GetMapping
    public ResponseEntity<List<RES>> listar() {
        return ResponseEntity.ok(aRespuestas(getServicio().listar()));
    }

    /** GET /api/xxx/{id}  ->  200, o 404 si no existe */
    @GetMapping("/{id}")
    public ResponseEntity<RES> buscarPorId(@PathVariable ID id) {
        return ResponseEntity.ok(aRespuesta(getServicio().buscarPorId(id)));
    }

    /**
     * POST /api/xxx  ->  201 Created
     *
     * La cabecera Location dice donde quedo el recurso nuevo, por ejemplo
     * Location: http://localhost:8080/api/roles/7
     */
    @PostMapping
    public ResponseEntity<RES> crear(@Valid @RequestBody REQ request) {
        T creada = getServicio().crear(aEntidad(request));
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obtenerId(creada))
                .toUri();
        return ResponseEntity.created(ubicacion).body(aRespuesta(creada));
    }

    /** PUT /api/xxx/{id}  ->  200 con el registro actualizado */
    @PutMapping("/{id}")
    public ResponseEntity<RES> actualizar(@PathVariable ID id, @Valid @RequestBody REQ request) {
        T actualizada = getServicio().actualizar(id, aEntidad(request));
        return ResponseEntity.ok(aRespuesta(actualizada));
    }

    /** DELETE /api/xxx/{id}  ->  204 No Content (sin cuerpo) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable ID id) {
        getServicio().eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.refricentro.siver.controller;

import com.refricentro.siver.dto.PaginaResponse;
import com.refricentro.siver.exception.ErrorRespuesta;
import com.refricentro.siver.service.CrudService;
import com.refricentro.siver.util.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
 * Endpoints:
 *   GET    /api/xxx            lista completa (para combos)
 *   GET    /api/xxx/paginado   una pagina: ?page=0&size=10&sort=campo,desc&buscar=
 *   GET    /api/xxx/{id}
 *   POST   /api/xxx
 *   PUT    /api/xxx/{id}
 *   DELETE /api/xxx/{id}
 *
 * Las anotaciones @Operation y @ApiResponse de aqui las heredan TODOS los
 * controladores, asi Swagger documenta los 6 endpoints de cada entidad.
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
    @Operation(summary = "Lista todos los registros",
            description = "Sin paginar. Pensado para llenar combos (mat-select) del frontend.")
    @ApiResponse(responseCode = "200", description = "Lista completa")
    @ApiResponse(responseCode = "401", description = "Falta el token o no es valido",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public ResponseEntity<List<RES>> listar() {
        return ResponseEntity.ok(aRespuestas(getServicio().listar()));
    }

    /**
     * GET /api/xxx/paginado?page=0&size=10&sort=campo,desc&buscar=texto
     *
     * La primera pagina es la 0. Si no se manda size se usan 10 registros, y
     * aunque se pida size=1000 Spring lo limita a 50
     * (spring.data.web.pageable.max-page-size).
     *
     * La respuesta trae totalElements y totalPages: con eso Angular arma el
     * paginador sin tener que traer la tabla completa.
     */
    @GetMapping("/paginado")
    @Operation(summary = "Lista paginada, con busqueda opcional",
            description = "page empieza en 0. size maximo 50. sort=campo,asc|desc. "
                    + "buscar filtra por texto con una consulta JPQL de parametros nombrados.")
    @ApiResponse(responseCode = "200", description = "Una pagina con content, totalElements y totalPages")
    @ApiResponse(responseCode = "400", description = "Campo de orden (sort) que no existe",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "401", description = "Falta el token o no es valido",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public ResponseEntity<PaginaResponse<RES>> listarPaginado(
            @Parameter(description = "Texto a buscar (opcional)")
            @RequestParam(required = false) String buscar,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(PaginaResponse.de(
                getServicio().listarPaginado(buscar, pageable), this::aRespuesta));
    }

    /** GET /api/xxx/{id}  ->  200, o 404 si no existe */
    @GetMapping("/{id}")
    @Operation(summary = "Busca un registro por su id")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un registro con ese id",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
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
    @Operation(summary = "Crea un registro",
            description = "Responde 201 con la cabecera Location apuntando al recurso nuevo.")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (detalle por campo en 'campos')",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "409", description = "Choca con una restriccion de la base (dato repetido)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
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
    @Operation(summary = "Actualiza un registro")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "No existe un registro con ese id",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public ResponseEntity<RES> actualizar(@PathVariable ID id, @Valid @RequestBody REQ request) {
        T actualizada = getServicio().actualizar(id, aEntidad(request));
        return ResponseEntity.ok(aRespuesta(actualizada));
    }

    /** DELETE /api/xxx/{id}  ->  204 No Content (sin cuerpo) */
    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un registro",
            description = "Borrado logico (activo = false) o fisico segun la entidad.")
    @ApiResponse(responseCode = "204", description = "Eliminado, sin cuerpo")
    @ApiResponse(responseCode = "404", description = "No existe un registro con ese id",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "409", description = "Tiene registros que dependen de el",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public ResponseEntity<Void> eliminar(@PathVariable ID id) {
        getServicio().eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

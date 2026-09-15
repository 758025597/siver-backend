# SIVER - Organizacion del equipo (API REST)

## 1. Estructura de carpetas

```
com/refricentro/siver/
├── modelos/        Las 11 entidades JPA  (YA ESTA HECHO)
│   └── enums/
├── repository/     Interfaces JpaRepository       <- una por entidad
├── service/        Interfaces de servicio + base generica
│   └── impl/       Implementaciones + logica de negocio
├── controller/     Los @RestController
├── dto/            Request / Response de la API
├── exception/      Excepciones propias + manejador global
└── config/         CORS y demas configuracion
```

Cada carpeta tiene su README.md explicando que va adentro.

Por cada entidad se escriben 5 archivos:

| Capa | Archivo | Ejemplo |
|---|---|---|
| repository | `XRepository` | `ProductoRepository` |
| service | `XService` | `ProductoService` |
| service.impl | `XServiceImpl` | `ProductoServiceImpl` |
| controller | `XController` | `ProductoController` |
| dto | `XRequest` / `XResponse` | `ProductoRequest` |

## 2. Que operaciones lleva cada entidad y por que

Esto NO es opinion: sale de las reglas de la propia base de datos.
10 de las 13 llaves foraneas son ON DELETE RESTRICT, o sea MySQL
impide el borrado fisico. Y 6 tablas tienen columna `activo`, que
existe justamente para desactivar sin borrar.

| Entidad | C | R | U | D | Tipo de DELETE | Motivo |
|---|:-:|:-:|:-:|:-:|---|---|
| rol | si | si | si | si | SOFT (`activo`) | usuario->rol es RESTRICT |
| usuario | si | si | si | si | SOFT (`activo`) | Firmo ventas y ordenes: se pierde trazabilidad |
| cliente | si | si | si | si | SOFT (`activo`) | Tiene ventas y ordenes asociadas |
| categoria | si | si | si | si | SOFT (`activo`) | producto->categoria es RESTRICT |
| proveedor | si | si | si | si | SOFT (`activo`) | Se perderia a quien se le compro |
| producto | si | si | si | si | SOFT (`activo`) | Esta en detalle_venta (RESTRICT) |
| venta | si | si | NO | si | SOFT (`estado=ANULADA`) | Comprobante tributario: se anula, no se borra |
| detalle_venta | con la venta | si | NO | NO | ninguno | Los triggers ya descontaron stock |
| estado_orden | si | si | si | si | HARD (si no se usa) | Catalogo fijo, es la unica sin `activo` |
| orden_servicio | si | si | si | si | SOFT (via `id_estado`) | El flujo de estados es su ciclo de vida |
| movimiento_inventario | automatico | si | NO | NO | ninguno | Bitacora de auditoria, la escriben los triggers |

Los tres casos a explicar en la sustentacion:
- `venta` no lleva UPDATE ni DELETE: una boleta emitida no se corrige
  ni se borra, se ANULA y se emite otra. Por eso la tabla trae
  `estado ENUM('EMITIDA','ANULADA')` en vez de `activo`.
- `movimiento_inventario` solo lleva READ: una bitacora que se puede
  editar no sirve como bitacora.
- `detalle_venta` no tiene CRUD propio: si borraras una linea suelta,
  el stock quedaria descuadrado para siempre.

## 3. Reparto por modulos (5 integrantes)

Se reparte por MODULO, no por entidad suelta, porque hay entidades que
tienen que hacerse juntas (venta y detalle_venta comparten triggers).

| # | Modulo | Entidades | Peso |
|---|---|---|---|
| 1 | Seguridad | rol, usuario | Media |
| 2 | Maestros | categoria, proveedor, cliente | Media (3 simples, mismo patron) |
| 3 | Inventario | producto, movimiento_inventario | Alta (producto es la mas pesada) |
| 4 | Ventas | venta, detalle_venta | Alta (triggers y anulacion) |
| 5 | Servicio tecnico | estado_orden, orden_servicio | Media |

## 4. ORDEN DE TRABAJO (importante)

**Las clases genericas se hacen PRIMERO y las hace UNA sola persona.**
Nadie puede empezar su modulo hasta que esten listas, porque todos heredan
de ellas. Si cada uno hace su propia version, no se va a poder integrar.

Paso 0 (una persona, antes que nadie):
- `service/CrudService.java`           interfaz generica
- `service/Activable.java`             interfaz para las 6 entidades con `activo`
- `service/impl/ServicioGenerico.java` clase abstracta con el CRUD
- `service/impl/ServicioSoftDelete.java` sobrescribe eliminar()
- `controller/ControladorGenerico.java` clase abstracta
- `exception/` el manejador global
- `config/` CORS

Paso 1 (los 5 en paralelo): cada quien su modulo heredando de lo anterior.

## 5. Reglas de git

- Una rama por modulo: `feature/seguridad`, `feature/inventario`, etc.
- NADIE trabaja directo sobre main.
- Antes de empezar: `git pull origin main`
- Tocar SOLO los archivos del modulo propio.
- El pom.xml NO se toca. Si IntelliJ ofrece "agregar soporte de Kotlin",
  decir que NO: eso apaga Lombok en todo el proyecto y deja las entidades
  sin getters, y el error aparece recien cuando alguien escribe un controlador.

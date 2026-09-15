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

| # | Modulo | Responsable | Rama | Entidades | Archivos |
|---|---|---|---|---|---|
| 1 | Seguridad | __________ | `feature/seguridad` | rol, usuario | 12 |
| 2 | Maestros | __________ | `feature/maestros` | categoria, proveedor, cliente | 18 |
| 3 | Inventario | __________ | `feature/inventario` | producto, movimiento_inventario | 11 |
| 4 | Ventas | __________ | `feature/ventas` | venta, detalle_venta | 9 |
| 5 | Servicio tecnico | __________ | `feature/servicio-tecnico` | estado_orden, orden_servicio | 12 |

Esta parejo: el modulo 2 tiene mas archivos pero son repetitivos, y el 4
tiene menos archivos pero la logica mas dificil.

### Regla general: 6 archivos por entidad

```
repository/XRepository.java
service/XService.java
service/impl/XServiceImpl.java
controller/XController.java
dto/XRequest.java
dto/XResponse.java
```

### Modulo 1 - Seguridad  (rol, usuario)

```
repository/RolRepository.java          repository/UsuarioRepository.java
service/RolService.java                service/UsuarioService.java
service/impl/RolServiceImpl.java       service/impl/UsuarioServiceImpl.java
controller/RolController.java          controller/UsuarioController.java
dto/RolRequest.java                    dto/UsuarioRequest.java
dto/RolResponse.java                   dto/UsuarioResponse.java
```

CUIDADO: `UsuarioResponse` NO puede llevar el campo `clave`. Si devuelves la
entidad tal cual, la API publica las contrasenas de todos.
El DELETE de ambas es SOFT (columna `activo`).

### Modulo 2 - Maestros  (categoria, proveedor, cliente)

Los 6 archivos de siempre, por cada una de las tres. Son 18 en total, pero
las tres son casi identicas: haz `categoria` completa, y las otras dos salen
copiando el patron.
Las tres llevan DELETE SOFT (columna `activo`).

Detalle de cada una:
- categoria  -> nombre es UNIQUE, validar duplicado antes de crear
- proveedor  -> `ruc` es CHAR(11) y UNIQUE, validar 11 digitos
- cliente    -> `tipo_documento` es ENUM('DNI','RUC','CE')

### Modulo 3 - Inventario  (producto, movimiento_inventario)

```
producto  -> los 6 archivos completos
movimiento_inventario -> SOLO 5, sin XRequest:
    repository/MovimientoInventarioRepository.java
    service/MovimientoInventarioService.java
    service/impl/MovimientoInventarioServiceImpl.java
    controller/MovimientoInventarioController.java
    dto/MovimientoInventarioResponse.java
```

Por que `movimiento_inventario` no lleva Request: es una BITACORA. No se crea
a mano, la escriben los triggers de MySQL. Solo se consulta.

CUIDADO con `producto`: el stock lo modifican los triggers por detras. Si
cargas un Producto en memoria y lo guardas despues de una venta, escribes el
stock viejo y borras lo que hizo el trigger. Siempre recargar antes de guardar.
Ademas hay un CHECK: precio_venta >= precio_compra.

### Modulo 4 - Ventas  (venta, detalle_venta)

```
venta -> los 6 archivos, PERO:
    en vez de DELETE /ventas/{id}  va  PATCH /ventas/{id}/anular
    (cambia estado a ANULADA, no borra)

detalle_venta -> solo 3, sin controlador propio:
    repository/DetalleVentaRepository.java
    dto/DetalleVentaRequest.java    (va dentro de VentaRequest)
    dto/DetalleVentaResponse.java   (va dentro de VentaResponse)
```

Por que `venta` no se borra ni se edita: es un comprobante tributario.
Una boleta emitida no se corrige, se ANULA y se emite otra.

Por que `detalle_venta` no tiene CRUD propio: se crea junto con su venta, y
sus triggers ya descontaron el stock. Borrar una linea suelta dejaria el
stock descuadrado para siempre.

El trigger valida el stock: si no alcanza, MySQL lanza
'Stock insuficiente para completar la venta'. Ese error hay que atraparlo
en el manejador global y devolverlo como 400, no como 500.

### Modulo 5 - Servicio tecnico  (estado_orden, orden_servicio)

```
estado_orden     -> los 6 archivos.  UNICO caso de DELETE FISICO (hard)
orden_servicio   -> los 6 archivos.  DELETE SOFT via id_estado
```

Por que `estado_orden` si admite hard delete: es un catalogo fijo de 4 o 5
filas y es la unica tabla sin columna `activo`. Igual solo se podra borrar un
estado que ninguna orden este usando, porque la FK es ON DELETE RESTRICT.

CUIDADO con `orden_servicio`: tiene el trigger `trg_orden_before_update`, que
pone `fecha_entrega_real` sola cuando la orden pasa a un estado final.

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

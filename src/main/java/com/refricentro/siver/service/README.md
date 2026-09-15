# service

Interfaces de servicio: QUE hace cada modulo, sin decir como.

Aqui vive tambien la BASE GENERICA que comparten todos:
- `CrudService<T, ID>`  -> declara listar, buscarPorId, crear, actualizar, eliminar
- `Activable`           -> interfaz para las 6 entidades con columna `activo`

Cada servicio concreto extiende `CrudService` y agrega solo lo suyo.
Ejemplo: `ProductoService extends CrudService<Producto, Integer>` y ademas
declara `List<Producto> buscarConStockBajo()`.

La logica de negocio va en impl/, no aqui.

# dto

Objetos que viajan por la API. Sirven para NO exponer las entidades directamente.

Por que importan aqui: la entidad `Usuario` tiene el campo `clave`. Si devuelves
la entidad tal cual, la API estaria publicando las contrasenas.

Dos por entidad:
- `ProductoRequest`  -> lo que ENTRA (sin id, con las validaciones @NotBlank, @Email...)
- `ProductoResponse` -> lo que SALE (sin campos sensibles)

La conversion entidad <-> dto se hace en el service.

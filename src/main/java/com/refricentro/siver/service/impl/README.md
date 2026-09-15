# service.impl

Implementaciones de los servicios. Aqui SI va la logica de negocio.
Van anotadas con `@Service` y sus metodos de escritura con `@Transactional`.

Clases base que se hacen UNA sola vez y heredan todas las demas:
- `ServicioGenerico<T, ID>`   -> abstracta, implementa el CRUD con JpaRepository
- `ServicioSoftDelete<T, ID>` -> hereda de la anterior y SOBRESCRIBE eliminar()
                                 para hacer setActivo(false) en vez de borrar

Asi el CRUD se escribe una vez y cada entidad solo agrega lo propio.

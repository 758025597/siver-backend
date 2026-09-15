# repository

Interfaces de acceso a datos. Una por entidad.

Cada una extiende `JpaRepository<Entidad, Integer>` y va anotada con `@Repository`.
Aqui NO se escribe logica de negocio: solo consultas.

Archivos esperados (11):
RolRepository, UsuarioRepository, ClienteRepository, CategoriaRepository,
ProveedorRepository, ProductoRepository, VentaRepository, DetalleVentaRepository,
EstadoOrdenRepository, OrdenServicioRepository, MovimientoInventarioRepository

Ojo: como casi todo usa SOFT DELETE, aqui van los metodos que filtran por
estado, del tipo `findByActivoTrue()`. El listado normal NO debe devolver
los registros desactivados.

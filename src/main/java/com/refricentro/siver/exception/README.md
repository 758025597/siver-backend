# exception

Manejo de errores de la API.

- Excepciones propias: `RecursoNoEncontradoException`, `RecursoDuplicadoException`
- `ManejadorGlobalErrores` anotado con `@RestControllerAdvice`: atrapa las
  excepciones y las convierte en una respuesta JSON uniforme.

Sin esto, un producto inexistente devuelve un stacktrace de 200 lineas en vez
de un 404 limpio.

Importante para este proyecto: los CHECK y los triggers de MySQL lanzan errores
de base de datos. Aqui es donde se traducen a mensajes entendibles,
por ejemplo "Stock insuficiente para completar la venta".

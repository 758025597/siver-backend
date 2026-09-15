# controller

Los `@RestController`. Solo reciben la peticion, llaman al service y devuelven
la respuesta. NO llevan logica de negocio ni tocan el repository directamente.

Convencion de rutas: `/api/productos`, `/api/usuarios` (plural, en minuscula).

Aqui va tambien `ControladorGenerico<T, ID>`: una clase abstracta con los
@GetMapping / @PostMapping / @PutMapping / @DeleteMapping comunes, de la que
heredan los 11 controladores.

Cada metodo devuelve `ResponseEntity` con el codigo correcto:
200 OK, 201 Created, 204 No Content, 404 Not Found, 400 Bad Request.

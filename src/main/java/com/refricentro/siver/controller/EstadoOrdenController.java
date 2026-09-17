package com.refricentro.siver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estados-orden")
@RequiredArgsConstructor
public class EstadoOrdenController {
    private final EstadoOrdenService estadoOrdenService;

    @GetMapping
    public ResponseEntity<List<EstadoOrden>> listar() {
        return ResponseEntity.ok(estadoOrdenService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoOrden> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(estadoOrdenService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EstadoOrden> crear(@RequestBody EstadoOrden estadoOrden) {
        EstadoOrden nuevo = estadoOrdenService.crear(estadoOrden);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoOrden> actualizar(@PathVariable Integer id,
                                                  @RequestBody EstadoOrden estadoOrden) {
        return ResponseEntity.ok(estadoOrdenService.actualizar(id, estadoOrden));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        estadoOrdenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

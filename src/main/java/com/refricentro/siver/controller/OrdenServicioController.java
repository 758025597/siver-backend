package com.refricentro.siver.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes-servicio")
@RequiredArgsConstructor

public class OrdenServicioController {
    private final OrdenServicioService ordenServicioService;

    @GetMapping
    public ResponseEntity<List<OrdenServicio>> listar() {
        return ResponseEntity.ok(ordenServicioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenServicio> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenServicioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdenServicio> crear(@RequestBody OrdenServicio ordenServicio) {
        OrdenServicio nueva = ordenServicioService.crear(ordenServicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenServicio> actualizar(@PathVariable Integer id,
                                                    @RequestBody OrdenServicio ordenServicio) {
        return ResponseEntity.ok(ordenServicioService.actualizar(id, ordenServicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ordenServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.hotel.reservas.controller;

import com.hotel.reservas.model.Caracteristica;
import com.hotel.reservas.service.CaracteristicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caracteristicas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CaracteristicaController {

    private final CaracteristicaService caracteristicaService;

    @GetMapping
    public ResponseEntity<List<Caracteristica>> getAll() {
        return ResponseEntity.ok(caracteristicaService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Caracteristica c) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(caracteristicaService.crear(c));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Caracteristica c) {
        try {
            return ResponseEntity.ok(caracteristicaService.editar(id, c));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            caracteristicaService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Característica eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}

package com.hotel.reservas.controller;

import com.hotel.reservas.model.Caracteristica;
import com.hotel.reservas.repository.CaracteristicaRepository;
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

    private final CaracteristicaRepository repo;

    @GetMapping
    public ResponseEntity<List<Caracteristica>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Caracteristica c) {
        if (repo.existsByNombre(c.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe una característica con ese nombre"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Caracteristica c) {
        return repo.findById(id).map(existing -> {
            existing.setNombre(c.getNombre());
            existing.setIcono(c.getIcono());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Característica no encontrada"));
        }
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Característica eliminada"));
    }
}

package com.hotel.reservas.controller;

import com.hotel.reservas.model.Politica;
import com.hotel.reservas.service.PoliticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/politicas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PoliticaController {

    private final PoliticaService politicaService;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Politica>> getByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(politicaService.getByProducto(productoId));
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> crear(@PathVariable Long productoId, @RequestBody Politica politica) {
        try {
            return ResponseEntity.ok(politicaService.crear(productoId, politica));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Politica politica) {
        try {
            return ResponseEntity.ok(politicaService.editar(id, politica));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            politicaService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Política eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

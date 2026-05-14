package com.hotel.reservas.controller;

import com.hotel.reservas.model.Politica;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.repository.PoliticaRepository;
import com.hotel.reservas.repository.ProductoRepository;
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

    private final PoliticaRepository politicaRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Politica>> getByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(politicaRepository.findByProductoId(productoId));
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> crear(@PathVariable Long productoId, @RequestBody Politica politica) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        politica.setProducto(producto);
        return ResponseEntity.ok(politicaRepository.save(politica));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        politicaRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Política eliminada"));
    }
}

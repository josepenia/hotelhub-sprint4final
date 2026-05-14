package com.hotel.reservas.controller;

import com.hotel.reservas.dto.ProductoDTO;
import com.hotel.reservas.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/productos/aleatorios — hasta 10 productos random (para el Home)
    @GetMapping("/aleatorios")
    public ResponseEntity<List<ProductoDTO>> getAleatorios() {
        return ResponseEntity.ok(productoService.getProductosAleatorios());
    }

    // GET /api/productos?page=0&size=10 — listado paginado (para admin)
    @GetMapping
    public ResponseEntity<Page<ProductoDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productoService.getAllProductos(PageRequest.of(page, size)));
    }

    // GET /api/productos/{id} — detalle
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    // POST /api/productos — crear producto
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProductoDTO dto) {
        try {
            ProductoDTO creado = productoService.crearProducto(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/productos/{id} — eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

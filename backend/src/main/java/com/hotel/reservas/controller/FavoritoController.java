package com.hotel.reservas.controller;

import com.hotel.reservas.model.Favorito;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.FavoritoRepository;
import com.hotel.reservas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FavoritoController {

    private final FavoritoRepository favoritoRepository;
    private final ProductoRepository productoRepository;

    // Mis favoritos
    @GetMapping
    public ResponseEntity<?> getMisFavoritos(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        List<Favorito> favs = favoritoRepository.findByUsuarioId(usuario.getId());
        List<Map<String, Object>> result = favs.stream().map(f -> Map.of(
            "id", (Object) f.getId(),
            "productoId", f.getProducto().getId(),
            "nombre", f.getProducto().getNombre(),
            "descripcion", f.getProducto().getDescripcion(),
            "imagenes", f.getProducto().getImagenes() != null ? f.getProducto().getImagenes() : List.of(),
            "categoriaNombre", f.getProducto().getCategoria() != null ? f.getProducto().getCategoria().getNombre() : ""
        )).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // Ids de mis favoritos (para saber cuáles están marcados en el home)
    @GetMapping("/ids")
    public ResponseEntity<?> getMisFavoritosIds(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.ok(List.of());
        List<Long> ids = favoritoRepository.findByUsuarioId(usuario.getId())
                .stream().map(f -> f.getProducto().getId()).collect(Collectors.toList());
        return ResponseEntity.ok(ids);
    }

    // Toggle favorito
    @PostMapping("/toggle/{productoId}")
    public ResponseEntity<?> toggleFavorito(@AuthenticationPrincipal Usuario usuario,
                                             @PathVariable Long productoId) {
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión"));

        if (favoritoRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId)) {
            favoritoRepository.deleteByUsuarioIdAndProductoId(usuario.getId(), productoId);
            return ResponseEntity.ok(Map.of("favorito", false));
        } else {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            favoritoRepository.save(new Favorito(null, usuario, producto));
            return ResponseEntity.ok(Map.of("favorito", true));
        }
    }
}

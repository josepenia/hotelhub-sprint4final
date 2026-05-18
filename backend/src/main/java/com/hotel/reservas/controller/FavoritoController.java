package com.hotel.reservas.controller;

import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping
    public ResponseEntity<?> getMisFavoritos(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(favoritoService.getMisFavoritos(usuario.getId()));
    }

    @GetMapping("/ids")
    public ResponseEntity<?> getMisFavoritosIds(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(favoritoService.getMisFavoritosIds(usuario.getId()));
    }

    @PostMapping("/toggle/{productoId}")
    public ResponseEntity<?> toggleFavorito(@AuthenticationPrincipal Usuario usuario,
                                             @PathVariable Long productoId) {
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión"));
        try {
            return ResponseEntity.ok(favoritoService.toggleFavorito(usuario, productoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

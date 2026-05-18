package com.hotel.reservas.controller;

import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.service.PuntuacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/puntuaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PuntuacionController {

    private final PuntuacionService puntuacionService;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Map<String, Object>>> getPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(puntuacionService.getPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        return ResponseEntity.ok(puntuacionService.getPromedio(productoId));
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> puntuar(@AuthenticationPrincipal Usuario usuario,
                                     @PathVariable Long productoId,
                                     @RequestBody Map<String, Object> body) {
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión"));
        try {
            int estrellas = (int) body.get("estrellas");
            String comentario = (String) body.getOrDefault("comentario", "");
            return ResponseEntity.ok(puntuacionService.puntuar(usuario, productoId, estrellas, comentario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}

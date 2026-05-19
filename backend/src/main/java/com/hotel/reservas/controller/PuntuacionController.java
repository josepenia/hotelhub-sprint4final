package com.hotel.reservas.controller;

import com.hotel.reservas.dto.PuntuacionDTO;
import com.hotel.reservas.exception.UnauthorizedException;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.service.PuntuacionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<PuntuacionDTO.Response>> getPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(puntuacionService.getPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        return ResponseEntity.ok(puntuacionService.getPromedio(productoId));
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<Map<String, String>> puntuar(@AuthenticationPrincipal Usuario usuario,
                                                        @PathVariable Long productoId,
                                                        @Valid @RequestBody PuntuacionDTO.Request req) {
        if (usuario == null) throw new UnauthorizedException("Debe iniciar sesión");
        return ResponseEntity.ok(puntuacionService.puntuar(usuario, productoId, req));
    }
}

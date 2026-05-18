package com.hotel.reservas.controller;

import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/producto/{productoId}/fechas-ocupadas")
    public ResponseEntity<List<Map<String, String>>> getFechasOcupadas(@PathVariable Long productoId) {
        return ResponseEntity.ok(reservaService.getFechasOcupadas(productoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@AuthenticationPrincipal Usuario usuario,
                                   @RequestBody Map<String, String> body) {
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión para reservar"));
        try {
            Long productoId = Long.parseLong(body.get("productoId"));
            LocalDate inicio = LocalDate.parse(body.get("fechaInicio"));
            LocalDate fin = LocalDate.parse(body.get("fechaFin"));
            return ResponseEntity.ok(reservaService.crearReserva(usuario, productoId, inicio, fin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<?> misReservas(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(reservaService.getMisReservas(usuario.getId()));
    }
}

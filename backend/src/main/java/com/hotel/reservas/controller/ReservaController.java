package com.hotel.reservas.controller;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.exception.UnauthorizedException;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ReservaDTO.Response> crear(@AuthenticationPrincipal Usuario usuario,
                                                      @Valid @RequestBody ReservaDTO.Request req) {
        if (usuario == null) throw new UnauthorizedException("Debe iniciar sesión para reservar");
        return ResponseEntity.ok(reservaService.crearReserva(usuario, req));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO.Response>> misReservas(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) throw new UnauthorizedException("Debe iniciar sesión");
        return ResponseEntity.ok(reservaService.getMisReservas(usuario.getId()));
    }
}

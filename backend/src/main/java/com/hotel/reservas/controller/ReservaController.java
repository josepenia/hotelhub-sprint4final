package com.hotel.reservas.controller;

import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Reserva;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.ProductoRepository;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;

    // Fechas ocupadas de un producto (público)
    @GetMapping("/producto/{productoId}/fechas-ocupadas")
    public ResponseEntity<List<Map<String, String>>> getFechasOcupadas(@PathVariable Long productoId) {
        List<Reserva> reservas = reservaRepository.findByProductoId(productoId);
        List<Map<String, String>> fechas = reservas.stream().map(r -> Map.of(
            "inicio", r.getFechaInicio().toString(),
            "fin", r.getFechaFin().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(fechas);
    }

    // Crear reserva
    @PostMapping
    public ResponseEntity<?> crear(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody Map<String, String> body) {

        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión para reservar"));

        Long productoId = Long.parseLong(body.get("productoId"));
        LocalDate inicio = LocalDate.parse(body.get("fechaInicio"));
        LocalDate fin = LocalDate.parse(body.get("fechaFin"));

        if (!inicio.isBefore(fin))
            return ResponseEntity.badRequest().body(Map.of("error", "La fecha de inicio debe ser anterior a la de fin"));

        List<Reserva> conflictos = reservaRepository.findConflictos(productoId, inicio, fin);
        if (!conflictos.isEmpty())
            return ResponseEntity.status(409).body(Map.of("error", "Las fechas seleccionadas no están disponibles"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Reserva reserva = new Reserva(null, producto, usuario, inicio, fin);
        Reserva guardada = reservaRepository.save(reserva);

        return ResponseEntity.ok(Map.of(
            "id", guardada.getId(),
            "productoId", producto.getId(),
            "productoNombre", producto.getNombre(),
            "fechaInicio", inicio.toString(),
            "fechaFin", fin.toString(),
            "mensaje", "Reserva realizada correctamente"
        ));
    }

    // Historial de reservas del usuario autenticado
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> misReservas(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Map<String, Object>> result = reservaRepository.findByUsuarioId(usuario.getId())
            .stream()
            .sorted((a, b) -> b.getFechaInicio().compareTo(a.getFechaInicio()))
            .map(r -> {
                String imagen = (r.getProducto().getImagenes() != null && !r.getProducto().getImagenes().isEmpty())
                    ? r.getProducto().getImagenes().get(0) : "";
                return Map.<String, Object>of(
                    "id", r.getId(),
                    "productoId", r.getProducto().getId(),
                    "productoNombre", r.getProducto().getNombre(),
                    "productoImagen", imagen,
                    "categoriaNombre", r.getProducto().getCategoria() != null ? r.getProducto().getCategoria().getNombre() : "",
                    "fechaInicio", r.getFechaInicio().toString(),
                    "fechaFin", r.getFechaFin().toString(),
                    "estado", r.getFechaFin().isBefore(LocalDate.now()) ? "Finalizada" : "Activa"
                );
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

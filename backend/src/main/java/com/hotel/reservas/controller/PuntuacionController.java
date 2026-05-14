package com.hotel.reservas.controller;

import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Puntuacion;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.ProductoRepository;
import com.hotel.reservas.repository.PuntuacionRepository;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/puntuaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PuntuacionController {

    private final PuntuacionRepository puntuacionRepository;
    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Map<String, Object>>> getPorProducto(@PathVariable Long productoId) {
        List<Puntuacion> punts = puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId);
        List<Map<String, Object>> result = punts.stream().map(p -> Map.of(
            "id", (Object) p.getId(),
            "estrellas", p.getEstrellas(),
            "comentario", p.getComentario() != null ? p.getComentario() : "",
            "fecha", p.getFecha().toString(),
            "usuario", p.getUsuario().getNombre() + " " + p.getUsuario().getApellido()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        Double promedio = puntuacionRepository.promedioEstrellas(productoId);
        long total = puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId).size();
        return ResponseEntity.ok(Map.of(
            "promedio", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0,
            "total", total
        ));
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> puntuar(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long productoId,
            @RequestBody Map<String, Object> body) {

        if (usuario == null) return ResponseEntity.status(401).body(Map.of("error", "Debe iniciar sesión"));

        // Solo usuarios con reserva finalizada pueden puntuar
        if (!reservaRepository.usuarioTieneReservaFinalizada(usuario.getId(), productoId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Solo podés puntuar productos en los que hayas tenido una reserva finalizada"));
        }

        if (puntuacionRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId)) {
            return ResponseEntity.status(409).body(Map.of("error", "Ya puntuaste este producto"));
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Puntuacion p = new Puntuacion();
        p.setProducto(producto);
        p.setUsuario(usuario);
        p.setEstrellas((int) body.get("estrellas"));
        p.setComentario((String) body.getOrDefault("comentario", ""));
        puntuacionRepository.save(p);

        return ResponseEntity.ok(Map.of("mensaje", "Puntuación guardada"));
    }
}

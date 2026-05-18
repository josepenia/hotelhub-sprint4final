package com.hotel.reservas.service;

import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Reserva;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.ProductoRepository;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;

    public List<Map<String, String>> getFechasOcupadas(Long productoId) {
        return reservaRepository.findByProductoId(productoId)
                .stream().map(r -> Map.of(
                        "inicio", r.getFechaInicio().toString(),
                        "fin", r.getFechaFin().toString()
                )).collect(Collectors.toList());
    }

    public Map<String, Object> crearReserva(Usuario usuario, Long productoId,
                                             LocalDate inicio, LocalDate fin) {
        if (!inicio.isBefore(fin))
            throw new RuntimeException("La fecha de inicio debe ser anterior a la de fin");

        List<Reserva> conflictos = reservaRepository.findConflictos(productoId, inicio, fin);
        if (!conflictos.isEmpty())
            throw new RuntimeException("Las fechas seleccionadas no están disponibles");

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        Reserva reserva = new Reserva(null, producto, usuario, inicio, fin);
        Reserva guardada = reservaRepository.save(reserva);

        return Map.of(
                "id", guardada.getId(),
                "productoId", producto.getId(),
                "productoNombre", producto.getNombre(),
                "fechaInicio", inicio.toString(),
                "fechaFin", fin.toString(),
                "mensaje", "Reserva realizada correctamente"
        );
    }

    public List<Map<String, Object>> getMisReservas(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId)
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
                }).collect(Collectors.toList());
    }
}

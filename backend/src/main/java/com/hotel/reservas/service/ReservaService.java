package com.hotel.reservas.service;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.exception.BadRequestException;
import com.hotel.reservas.exception.ConflictException;
import com.hotel.reservas.exception.ResourceNotFoundException;
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

    public ReservaDTO.Response crearReserva(Usuario usuario, ReservaDTO.Request req) {
        if (!req.getFechaInicio().isBefore(req.getFechaFin()))
            throw new BadRequestException("La fecha de inicio debe ser anterior a la de fin");

        List<Reserva> conflictos = reservaRepository.findConflictos(req.getProductoId(), req.getFechaInicio(), req.getFechaFin());
        if (!conflictos.isEmpty())
            throw new ConflictException("Las fechas seleccionadas no están disponibles");

        Producto producto = productoRepository.findById(req.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + req.getProductoId()));

        Reserva reserva = new Reserva(null, producto, usuario, req.getFechaInicio(), req.getFechaFin());
        Reserva guardada = reservaRepository.save(reserva);

        ReservaDTO.Response res = new ReservaDTO.Response();
        res.setId(guardada.getId());
        res.setProductoId(producto.getId());
        res.setProductoNombre(producto.getNombre());
        res.setFechaInicio(req.getFechaInicio());
        res.setFechaFin(req.getFechaFin());
        res.setMensaje("Reserva realizada correctamente");
        return res;
    }

    public List<ReservaDTO.Response> getMisReservas(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId)
                .stream()
                .sorted((a, b) -> b.getFechaInicio().compareTo(a.getFechaInicio()))
                .map(r -> {
                    ReservaDTO.Response res = new ReservaDTO.Response();
                    res.setId(r.getId());
                    res.setProductoId(r.getProducto().getId());
                    res.setProductoNombre(r.getProducto().getNombre());
                    res.setProductoImagen(r.getProducto().getImagenes() != null && !r.getProducto().getImagenes().isEmpty()
                            ? r.getProducto().getImagenes().get(0) : "");
                    res.setCategoriaNombre(r.getProducto().getCategoria() != null ? r.getProducto().getCategoria().getNombre() : "");
                    res.setFechaInicio(r.getFechaInicio());
                    res.setFechaFin(r.getFechaFin());
                    res.setEstado(r.getFechaFin().isBefore(LocalDate.now()) ? "Finalizada" : "Activa");
                    return res;
                }).collect(Collectors.toList());
    }
}

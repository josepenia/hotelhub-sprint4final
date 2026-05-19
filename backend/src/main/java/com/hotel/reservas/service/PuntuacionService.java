package com.hotel.reservas.service;

import com.hotel.reservas.dto.PuntuacionDTO;
import com.hotel.reservas.exception.ConflictException;
import com.hotel.reservas.exception.ResourceNotFoundException;
import com.hotel.reservas.exception.UnauthorizedException;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Puntuacion;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.ProductoRepository;
import com.hotel.reservas.repository.PuntuacionRepository;
import com.hotel.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuntuacionService {

    private final PuntuacionRepository puntuacionRepository;
    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;

    public List<PuntuacionDTO.Response> getPorProducto(Long productoId) {
        return puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId)
                .stream().map(p -> {
                    PuntuacionDTO.Response res = new PuntuacionDTO.Response();
                    res.setId(p.getId());
                    res.setEstrellas(p.getEstrellas());
                    res.setComentario(p.getComentario());
                    res.setFecha(p.getFecha());
                    res.setUsuario(p.getUsuario().getNombre() + " " + p.getUsuario().getApellido());
                    return res;
                }).collect(Collectors.toList());
    }

    public Map<String, Object> getPromedio(Long productoId) {
        Double promedio = puntuacionRepository.promedioEstrellas(productoId);
        long total = puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId).size();
        return Map.of(
                "promedio", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0,
                "total", total
        );
    }

    public Map<String, String> puntuar(Usuario usuario, Long productoId, PuntuacionDTO.Request req) {
        if (!reservaRepository.usuarioTieneReservaFinalizada(usuario.getId(), productoId))
            throw new UnauthorizedException("Solo podés puntuar productos en los que hayas tenido una reserva finalizada");

        if (puntuacionRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId))
            throw new ConflictException("Ya puntuaste este producto");

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        Puntuacion p = new Puntuacion();
        p.setProducto(producto);
        p.setUsuario(usuario);
        p.setEstrellas(req.getEstrellas());
        p.setComentario(req.getComentario());
        puntuacionRepository.save(p);

        return Map.of("mensaje", "Puntuación guardada");
    }
}

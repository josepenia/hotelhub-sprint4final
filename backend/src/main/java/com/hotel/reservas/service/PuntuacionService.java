package com.hotel.reservas.service;

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

    public List<Map<String, Object>> getPorProducto(Long productoId) {
        return puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId)
                .stream().map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "estrellas", p.getEstrellas(),
                        "comentario", p.getComentario() != null ? p.getComentario() : "",
                        "fecha", p.getFecha().toString(),
                        "usuario", p.getUsuario().getNombre() + " " + p.getUsuario().getApellido()
                )).collect(Collectors.toList());
    }

    public Map<String, Object> getPromedio(Long productoId) {
        Double promedio = puntuacionRepository.promedioEstrellas(productoId);
        long total = puntuacionRepository.findByProductoIdOrderByFechaDesc(productoId).size();
        return Map.of(
                "promedio", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0,
                "total", total
        );
    }

    public Map<String, String> puntuar(Usuario usuario, Long productoId, int estrellas, String comentario) {
        if (!reservaRepository.usuarioTieneReservaFinalizada(usuario.getId(), productoId))
            throw new RuntimeException("Solo podés puntuar productos en los que hayas tenido una reserva finalizada");

        if (puntuacionRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId))
            throw new RuntimeException("Ya puntuaste este producto");

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        Puntuacion p = new Puntuacion();
        p.setProducto(producto);
        p.setUsuario(usuario);
        p.setEstrellas(estrellas);
        p.setComentario(comentario);
        puntuacionRepository.save(p);

        return Map.of("mensaje", "Puntuación guardada");
    }
}

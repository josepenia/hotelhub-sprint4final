package com.hotel.reservas.service;

import com.hotel.reservas.model.Favorito;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.FavoritoRepository;
import com.hotel.reservas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ProductoRepository productoRepository;

    public List<Map<String, Object>> getMisFavoritos(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream().map(f -> Map.<String, Object>of(
                        "id", f.getId(),
                        "productoId", f.getProducto().getId(),
                        "nombre", f.getProducto().getNombre(),
                        "descripcion", f.getProducto().getDescripcion(),
                        "imagenes", f.getProducto().getImagenes() != null ? f.getProducto().getImagenes() : List.of(),
                        "categoriaNombre", f.getProducto().getCategoria() != null ? f.getProducto().getCategoria().getNombre() : ""
                )).collect(Collectors.toList());
    }

    public List<Long> getMisFavoritosIds(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream().map(f -> f.getProducto().getId()).collect(Collectors.toList());
    }

    public Map<String, Boolean> toggleFavorito(Usuario usuario, Long productoId) {
        if (favoritoRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId)) {
            favoritoRepository.deleteByUsuarioIdAndProductoId(usuario.getId(), productoId);
            return Map.of("favorito", false);
        } else {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));
            favoritoRepository.save(new Favorito(null, usuario, producto));
            return Map.of("favorito", true);
        }
    }
}

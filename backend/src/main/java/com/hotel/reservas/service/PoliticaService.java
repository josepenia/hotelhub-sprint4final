package com.hotel.reservas.service;

import com.hotel.reservas.model.Politica;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.repository.PoliticaRepository;
import com.hotel.reservas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoliticaService {

    private final PoliticaRepository politicaRepository;
    private final ProductoRepository productoRepository;

    public List<Politica> getByProducto(Long productoId) {
        return politicaRepository.findByProductoId(productoId);
    }

    public Politica crear(Long productoId, Politica politica) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));
        politica.setProducto(producto);
        return politicaRepository.save(politica);
    }

    public Politica editar(Long id, Politica politica) {
        Politica existing = politicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Política no encontrada con id: " + id));
        existing.setTitulo(politica.getTitulo());
        existing.setDescripcion(politica.getDescripcion());
        return politicaRepository.save(existing);
    }

    public void eliminar(Long id) {
        if (!politicaRepository.existsById(id))
            throw new RuntimeException("Política no encontrada con id: " + id);
        politicaRepository.deleteById(id);
    }
}

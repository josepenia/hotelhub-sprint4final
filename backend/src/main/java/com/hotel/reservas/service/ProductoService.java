package com.hotel.reservas.service;

import com.hotel.reservas.dto.ProductoDTO;
import com.hotel.reservas.model.Categoria;
import com.hotel.reservas.model.Caracteristica;
import com.hotel.reservas.model.Producto;
import com.hotel.reservas.repository.CaracteristicaRepository;
import com.hotel.reservas.repository.CategoriaRepository;
import com.hotel.reservas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public List<ProductoDTO> getProductosAleatorios() {
        return productoRepository.findRandomProductos()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<ProductoDTO> getAllProductos(Pageable pageable) {
        return productoRepository.findAll(pageable).map(this::toDTO);
    }

    public List<ProductoDTO> getByCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductoDTO getProductoById(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return toDTO(p);
    }

    public ProductoDTO crearProducto(ProductoDTO dto) {
        if (productoRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + dto.getNombre());
        }
        Producto p = toEntity(dto);
        return toDTO(productoRepository.save(p));
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setImagenes(p.getImagenes());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        if (p.getCaracteristicas() != null) {
            dto.setCaracteristicas(p.getCaracteristicas().stream().map(c -> {
                ProductoDTO.CaracteristicaInfo info = new ProductoDTO.CaracteristicaInfo();
                info.setId(c.getId());
                info.setNombre(c.getNombre());
                info.setIcono(c.getIcono());
                return info;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    private Producto toEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setImagenes(dto.getImagenes());
        if (dto.getCategoriaId() != null) {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            p.setCategoria(cat);
        }
        if (dto.getCaracteristicaIds() != null && !dto.getCaracteristicaIds().isEmpty()) {
            Set<Caracteristica> caracts = new HashSet<>(
                    caracteristicaRepository.findAllById(dto.getCaracteristicaIds()));
            p.setCaracteristicas(caracts);
        }
        return p;
    }
}

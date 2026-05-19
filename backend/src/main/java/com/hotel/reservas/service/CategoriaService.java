package com.hotel.reservas.service;

import com.hotel.reservas.exception.ConflictException;
import com.hotel.reservas.exception.ResourceNotFoundException;
import com.hotel.reservas.model.Categoria;
import com.hotel.reservas.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public Categoria getById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre()))
            throw new ConflictException("Ya existe una categoría con ese nombre");
        return categoriaRepository.save(categoria);
    }

    public Categoria editar(Long id, Categoria categoria) {
        Categoria existing = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        existing.setNombre(categoria.getNombre());
        existing.setDescripcion(categoria.getDescripcion());
        existing.setIcono(categoria.getIcono());
        existing.setImagenUrl(categoria.getImagenUrl());
        return categoriaRepository.save(existing);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        categoriaRepository.deleteById(id);
    }
}

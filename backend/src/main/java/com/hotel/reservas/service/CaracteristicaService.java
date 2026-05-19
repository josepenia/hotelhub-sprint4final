package com.hotel.reservas.service;

import com.hotel.reservas.exception.ConflictException;
import com.hotel.reservas.exception.ResourceNotFoundException;
import com.hotel.reservas.model.Caracteristica;
import com.hotel.reservas.repository.CaracteristicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaracteristicaService {

    private final CaracteristicaRepository caracteristicaRepository;

    public List<Caracteristica> getAll() {
        return caracteristicaRepository.findAll();
    }

    public Caracteristica crear(Caracteristica c) {
        if (caracteristicaRepository.existsByNombre(c.getNombre()))
            throw new ConflictException("Ya existe una característica con ese nombre");
        return caracteristicaRepository.save(c);
    }

    public Caracteristica editar(Long id, Caracteristica c) {
        Caracteristica existing = caracteristicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con id: " + id));
        existing.setNombre(c.getNombre());
        existing.setIcono(c.getIcono());
        return caracteristicaRepository.save(existing);
    }

    public void eliminar(Long id) {
        if (!caracteristicaRepository.existsById(id))
            throw new ResourceNotFoundException("Característica no encontrada con id: " + id);
        caracteristicaRepository.deleteById(id);
    }
}

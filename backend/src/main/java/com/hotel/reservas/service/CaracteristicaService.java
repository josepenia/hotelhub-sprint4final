package com.hotel.reservas.service;

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
        if (caracteristicaRepository.existsByNombre(c.getNombre())) {
            throw new RuntimeException("Ya existe una característica con ese nombre");
        }
        return caracteristicaRepository.save(c);
    }

    public Caracteristica editar(Long id, Caracteristica c) {
        Caracteristica existing = caracteristicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Característica no encontrada"));
        existing.setNombre(c.getNombre());
        existing.setIcono(c.getIcono());
        return caracteristicaRepository.save(existing);
    }

    public void eliminar(Long id) {
        if (!caracteristicaRepository.existsById(id)) {
            throw new RuntimeException("Característica no encontrada");
        }
        caracteristicaRepository.deleteById(id);
    }
}

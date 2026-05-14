package com.hotel.reservas.repository;

import com.hotel.reservas.model.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long> {
    boolean existsByNombre(String nombre);
}

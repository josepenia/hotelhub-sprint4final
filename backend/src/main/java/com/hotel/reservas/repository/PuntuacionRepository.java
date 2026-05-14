package com.hotel.reservas.repository;

import com.hotel.reservas.model.Puntuacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    List<Puntuacion> findByProductoIdOrderByFechaDesc(Long productoId);

    @Query("SELECT AVG(p.estrellas) FROM Puntuacion p WHERE p.producto.id = :productoId")
    Double promedioEstrellas(@Param("productoId") Long productoId);

    boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
}

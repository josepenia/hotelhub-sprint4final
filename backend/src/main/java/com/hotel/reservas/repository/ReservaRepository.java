package com.hotel.reservas.repository;

import com.hotel.reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByProductoId(Long productoId);

    List<Reserva> findByUsuarioId(Long usuarioId);

    // Fechas ocupadas para un producto
    @Query("SELECT r FROM Reserva r WHERE r.producto.id = :productoId " +
           "AND r.fechaInicio <= :fin AND r.fechaFin >= :inicio")
    List<Reserva> findConflictos(@Param("productoId") Long productoId,
                                  @Param("inicio") LocalDate inicio,
                                  @Param("fin") LocalDate fin);

    // Verifica si un usuario tiene reserva finalizada en un producto
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.usuario.id = :usuarioId " +
           "AND r.producto.id = :productoId AND r.fechaFin < CURRENT_DATE")
    boolean usuarioTieneReservaFinalizada(@Param("usuarioId") Long usuarioId,
                                           @Param("productoId") Long productoId);
}

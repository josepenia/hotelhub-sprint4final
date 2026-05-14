package com.hotel.reservas.repository;

import com.hotel.reservas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    // Devuelve hasta 10 productos en orden aleatorio
    @Query(value = "SELECT * FROM productos ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Producto> findRandomProductos();

    List<Producto> findByCategoriaId(Long categoriaId);
}

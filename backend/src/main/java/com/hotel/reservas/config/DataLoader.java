package com.hotel.reservas.config;

import com.hotel.reservas.model.*;
import com.hotel.reservas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin por defecto
        if (!usuarioRepository.existsByEmail("admin@hotelhub.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("HotelHub");
            admin.setEmail("admin@hotelhub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Usuario.Rol.ADMIN);
            usuarioRepository.save(admin);
        }

        // Categorías
        Categoria cat1 = categoriaRepository.save(new Categoria(null, "Suite", "Habitaciones de lujo con vistas panorámicas", "🏨", "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400"));
        Categoria cat2 = categoriaRepository.save(new Categoria(null, "Estándar", "Habitaciones confortables y bien equipadas", "🛏️", "https://images.unsplash.com/photo-1586105251261-72a756497a11?w=400"));
        Categoria cat3 = categoriaRepository.save(new Categoria(null, "Familiar", "Espacios amplios para familias", "👨‍👩‍👧‍👦", "https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?w=400"));
        Categoria cat4 = categoriaRepository.save(new Categoria(null, "Económica", "Precios accesibles sin sacrificar comodidad", "💰", "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=400"));

        // Características
        Caracteristica wifi = caracteristicaRepository.save(new Caracteristica(null, "Wi-Fi gratis", "📶"));
        Caracteristica ac = caracteristicaRepository.save(new Caracteristica(null, "Aire acondicionado", "❄️"));
        Caracteristica jacuzzi = caracteristicaRepository.save(new Caracteristica(null, "Jacuzzi", "🛁"));
        Caracteristica vista = caracteristicaRepository.save(new Caracteristica(null, "Vista al mar", "🌊"));
        Caracteristica desayuno = caracteristicaRepository.save(new Caracteristica(null, "Desayuno incluido", "🍳"));
        Caracteristica parking = caracteristicaRepository.save(new Caracteristica(null, "Estacionamiento", "🚗"));
        Caracteristica gym = caracteristicaRepository.save(new Caracteristica(null, "Gimnasio", "🏋️"));
        Caracteristica tv = caracteristicaRepository.save(new Caracteristica(null, "Smart TV", "📺"));

        // Productos
        Producto p1 = new Producto();
        p1.setNombre("Suite Presidencial");
        p1.setDescripcion("La habitación más exclusiva del hotel con vista al mar, jacuzzi privado y servicio personalizado las 24 horas.");
        p1.setCategoria(cat1);
        p1.setImagenes(List.of(
            "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800",
            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800",
            "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800",
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800"));
        p1.setCaracteristicas(Set.of(wifi, ac, jacuzzi, vista, desayuno));

        Producto p2 = new Producto();
        p2.setNombre("Suite Deluxe");
        p2.setDescripcion("Amplia suite con sala de estar, dormitorio principal y baño de mármol con bañera independiente.");
        p2.setCategoria(cat1);
        p2.setImagenes(List.of(
            "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800",
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800",
            "https://images.unsplash.com/photo-1561501900-3701fa6a0864?w=800",
            "https://images.unsplash.com/photo-1571508601891-ca5e7a713859?w=800",
            "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800"));
        p2.setCaracteristicas(Set.of(wifi, ac, tv, desayuno));

        Producto p3 = new Producto();
        p3.setNombre("Habitación Estándar Doble");
        p3.setDescripcion("Cómoda habitación con dos camas individuales, baño privado, TV y aire acondicionado.");
        p3.setCategoria(cat2);
        p3.setImagenes(List.of(
            "https://images.unsplash.com/photo-1631049552057-403cdb8f0658?w=800",
            "https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800",
            "https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=800",
            "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=800",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800"));
        p3.setCaracteristicas(Set.of(wifi, ac, tv));

        Producto p4 = new Producto();
        p4.setNombre("Habitación Estándar Queen");
        p4.setDescripcion("Habitación elegante con cama queen size, escritorio de trabajo y vistas al jardín.");
        p4.setCategoria(cat2);
        p4.setImagenes(List.of(
            "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800",
            "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=800",
            "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
            "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800"));
        p4.setCaracteristicas(Set.of(wifi, ac, tv, parking));

        Producto p5 = new Producto();
        p5.setNombre("Suite Familiar Amplia");
        p5.setDescripcion("Perfecta para familias, con dos habitaciones comunicadas, zona de juegos y baños independientes.");
        p5.setCategoria(cat3);
        p5.setImagenes(List.of(
            "https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?w=800",
            "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800",
            "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=800",
            "https://images.unsplash.com/photo-1571508601891-ca5e7a713859?w=800",
            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800"));
        p5.setCaracteristicas(Set.of(wifi, ac, tv, parking, desayuno));

        Producto p6 = new Producto();
        p6.setNombre("Habitación Económica Individual");
        p6.setDescripcion("Habitación funcional y confortable para viajeros solos con todas las comodidades básicas.");
        p6.setCategoria(cat4);
        p6.setImagenes(List.of(
            "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800",
            "https://images.unsplash.com/photo-1631049552057-403cdb8f0658?w=800",
            "https://images.unsplash.com/photo-1586105251261-72a756497a11?w=800",
            "https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=800",
            "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=800"));
        p6.setCaracteristicas(Set.of(wifi, tv));

        productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6));
        System.out.println("✅ Datos de prueba Sprint 2 cargados correctamente");
        System.out.println("👤 Admin: admin@hotelhub.com / admin123");
    }
}

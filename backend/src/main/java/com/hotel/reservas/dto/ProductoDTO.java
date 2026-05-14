package com.hotel.reservas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class ProductoDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    private List<String> imagenes;
    private Long categoriaId;
    private String categoriaNombre;
    private Set<Long> caracteristicaIds;
    private List<CaracteristicaInfo> caracteristicas;

    @Data
    public static class CaracteristicaInfo {
        private Long id;
        private String nombre;
        private String icono;
    }
}

package com.hotel.reservas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class ReservaDTO {

    @Data
    public static class Request {
        @NotNull(message = "El productoId es obligatorio")
        private Long productoId;

        @NotNull(message = "La fecha de inicio es obligatoria")
        private LocalDate fechaInicio;

        @NotNull(message = "La fecha de fin es obligatoria")
        private LocalDate fechaFin;
    }

    @Data
    public static class Response {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private String productoImagen;
        private String categoriaNombre;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String estado;
        private String mensaje;
    }
}

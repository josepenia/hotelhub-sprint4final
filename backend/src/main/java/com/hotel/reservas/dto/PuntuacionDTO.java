package com.hotel.reservas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class PuntuacionDTO {

    @Data
    public static class Request {
        @NotNull(message = "Las estrellas son obligatorias")
        @Min(value = 1, message = "Mínimo 1 estrella")
        @Max(value = 5, message = "Máximo 5 estrellas")
        private Integer estrellas;

        private String comentario;
    }

    @Data
    public static class Response {
        private Long id;
        private int estrellas;
        private String comentario;
        private LocalDate fecha;
        private String usuario;
    }
}

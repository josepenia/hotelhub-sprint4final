package com.hotel.reservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.USER;

    public enum Rol {
        USER, ADMIN
    }

    public String getInitials() {
        String n = nombre != null && !nombre.isEmpty() ? String.valueOf(nombre.charAt(0)) : "";
        String a = apellido != null && !apellido.isEmpty() ? String.valueOf(apellido.charAt(0)) : "";
        return (n + a).toUpperCase();
    }
}

package com.hotel.reservas.service;

import com.hotel.reservas.dto.AuthDTO.*;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.UsuarioRepository;
import com.hotel.reservas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRol(Usuario.Rol.USER);
        usuarioRepository.save(u);

        String token = jwtService.generateToken(u.getEmail(), u.getRol().name());
        return new AuthResponse(token, u.getNombre(), u.getApellido(),
                u.getEmail(), u.getRol().name(), u.getInitials());
    }

    public AuthResponse login(LoginRequest req) {
        Usuario u = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }

        String token = jwtService.generateToken(u.getEmail(), u.getRol().name());
        return new AuthResponse(token, u.getNombre(), u.getApellido(),
                u.getEmail(), u.getRol().name(), u.getInitials());
    }
}

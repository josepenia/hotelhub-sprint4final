package com.hotel.reservas.service;

import com.hotel.reservas.dto.UsuarioDTO;
import com.hotel.reservas.exception.BadRequestException;
import com.hotel.reservas.exception.ResourceNotFoundException;
import com.hotel.reservas.model.Usuario;
import com.hotel.reservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO getById(Long id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id)));
    }

    public UsuarioDTO cambiarRol(Long id, String nuevoRol) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        try {
            u.setRol(Usuario.Rol.valueOf(nuevoRol));
            return toDTO(usuarioRepository.save(u));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol inválido: " + nuevoRol);
        }
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol().name());
        return dto;
    }
}

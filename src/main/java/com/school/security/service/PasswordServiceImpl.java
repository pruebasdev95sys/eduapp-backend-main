package com.school.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.model.Empleado;
import com.school.model.Estudiante;
import com.school.security.dto.CambiarContrasenaDTO;
import com.school.security.dto.CambiarContrasenaLoginDTO;
import com.school.security.models.Usuario;
import com.school.service.EmpleadoService;
import com.school.service.EstudianteService;


@Service
@Transactional
public class PasswordServiceImpl implements IPasswordService {

    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean cambiarContrasenaUsuarioLogueado(String username, CambiarContrasenaDTO cambiarContrasenaDTO) {
        try {
            // Buscar el usuario por username
            Usuario usuario = usuarioService.findByUsername(username).orElse(null);
            if (usuario == null) {
                return false;
            }
            
            // Verificar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(cambiarContrasenaDTO.getContrasenaActual(), usuario.getPassword())) {
                return false;
            }
            
            // Cambiar la contraseña
            usuario.setPassword(passwordEncoder.encode(cambiarContrasenaDTO.getNuevaContrasena()));
            usuarioService.save(usuario);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean cambiarContrasenaDesdeLogin(CambiarContrasenaLoginDTO cambiarContrasenaLoginDTO) {
        try {
            // Buscar el usuario por CUI
            Usuario usuario = findUsuarioByCui(cambiarContrasenaLoginDTO.getCui());
            if (usuario == null) {
                return false;
            }
            
            // Cambiar la contraseña
            usuario.setPassword(passwordEncoder.encode(cambiarContrasenaLoginDTO.getNuevaContrasena()));
            usuarioService.save(usuario);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Usuario findUsuarioByCui(String cui) {
        // Buscar primero en empleados
        Empleado empleado = empleadoService.findByCui(cui).orElse(null);
        if (empleado != null && empleado.getUsuario() != null) {
            return empleado.getUsuario();
        }
        
        // Buscar en estudiantes
        Estudiante estudiante = estudianteService.findByCui(cui).orElse(null);
        if (estudiante != null && estudiante.getUsuario() != null) {
            return estudiante.getUsuario();
        }
        
        return null;
    }
}
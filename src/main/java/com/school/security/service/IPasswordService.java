package com.school.security.service;

import com.school.security.dto.CambiarContrasenaDTO;
import com.school.security.dto.CambiarContrasenaLoginDTO;
import com.school.security.models.Usuario;

public interface IPasswordService {
    boolean cambiarContrasenaUsuarioLogueado(String username, CambiarContrasenaDTO cambiarContrasenaDTO);
    boolean cambiarContrasenaDesdeLogin(CambiarContrasenaLoginDTO cambiarContrasenaLoginDTO);
    Usuario findUsuarioByCui(String cui);
}

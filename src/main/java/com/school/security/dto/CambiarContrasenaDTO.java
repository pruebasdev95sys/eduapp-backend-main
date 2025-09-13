package com.school.security.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CambiarContrasenaDTO {
    
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenaActual;
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;
    
    public CambiarContrasenaDTO() {}
    
    public CambiarContrasenaDTO(String contrasenaActual, String nuevaContrasena) {
        this.contrasenaActual = contrasenaActual;
        this.nuevaContrasena = nuevaContrasena;
    }
    
    // Getters y Setters
    public String getContrasenaActual() {
        return contrasenaActual;
    }
    
    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }
    
    public String getNuevaContrasena() {
        return nuevaContrasena;
    }
    
    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}
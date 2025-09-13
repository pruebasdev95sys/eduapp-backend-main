package com.school.security.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CambiarContrasenaLoginDTO {
    
    @NotBlank(message = "El CUI es obligatorio")
    private String cui;
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;
    
    public CambiarContrasenaLoginDTO() {}
    
    public CambiarContrasenaLoginDTO(String cui, String nuevaContrasena) {
        this.cui = cui;
        this.nuevaContrasena = nuevaContrasena;
    }
    
    // Getters y Setters
    public String getCui() {
        return cui;
    }
    
    public void setCui(String cui) {
        this.cui = cui;
    }
    
    public String getNuevaContrasena() {
        return nuevaContrasena;
    }
    
    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}
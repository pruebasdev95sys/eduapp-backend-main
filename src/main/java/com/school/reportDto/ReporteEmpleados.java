package com.school.reportDto;

import java.io.Serializable;
import java.util.stream.Collectors;

import com.school.model.Empleado;
import com.school.model.Especialidad;

public class ReporteEmpleados implements Serializable {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String cui;
    private String fechaNacimiento;
    private String celular;
    private String correo;
    private String sexo;
    private String domicilio;
    private String especialidades;
    private String username;
    private String password;
    private String mensajePassword;
    
    // Constructores
    public ReporteEmpleados() {}
    
    public ReporteEmpleados(Empleado empleado) {
        this.nombres = empleado.getNombres();
        this.apellidoPaterno = empleado.getApellidoPaterno();
        this.apellidoMaterno = empleado.getApellidoMaterno();
        this.cui = empleado.getCui();
        this.fechaNacimiento = empleado.getFechaNacimiento();
        this.celular = empleado.getCelular();
        this.correo = empleado.getCorreo();
        this.sexo = empleado.getSexo();
        this.domicilio = empleado.getDomicilio();
        
        // Formatear especialidades
        if (empleado.getEspecialidades() != null && !empleado.getEspecialidades().isEmpty()) {
            this.especialidades = empleado.getEspecialidades().stream()
                .map(Especialidad::getNombre)
                .collect(Collectors.joining(", "));
        } else {
            this.especialidades = "Sin especialidades asignadas";
        }
        
        if (empleado.getUsuario() != null) {
            this.username = empleado.getUsuario().getUsername();
            
            // Siempre mostrar el CUI como contraseña inicial con mensaje informativo
            this.password = empleado.getCui();
            this.mensajePassword = "Contraseña inicial (puede ser cambiada después del primer acceso)";
        } else {
            this.username = "No asignado";
            this.password = "No asignado";
            this.mensajePassword = "Usuario no creado";
        }
    }
    
    // Getters y setters para todos los campos
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }
    
    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }
    
    public String getCui() { return cui; }
    public void setCui(String cui) { this.cui = cui; }
    
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
    
    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getMensajePassword() { return mensajePassword; }
    public void setMensajePassword(String mensajePassword) { this.mensajePassword = mensajePassword; }
    
    private static final long serialVersionUID = 1L;
}
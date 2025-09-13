package com.school.security.dto;

import java.util.List;

public class UserInfoDTO {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String role;
    
    // Información adicional común
    private String fechaNacimiento;
    private String cui;
    private String domicilio;
    private String celular;
    private String sexo;
    private String correo;
    
    // Información específica para estudiantes
    private String grado;
    private String nivel;
    private String turno;
    private String aula;
    private String apoderadoNombre;
    private String apoderadoCelular;
    
    // Información específica para empleados
    private List<String> especialidades;
    
    // Constructores
    public UserInfoDTO() {}

    public UserInfoDTO(String nombres, String apellidoPaterno, String apellidoMaterno, String role) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.role = role;
    }

    // Constructor completo para estudiante
    public UserInfoDTO(String nombres, String apellidoPaterno, String apellidoMaterno, 
                      String role, String fechaNacimiento, String cui, String domicilio,
                      String celular, String sexo, String correo, String grado, 
                      String nivel, String turno, String aula, String apoderadoNombre, 
                      String apoderadoCelular) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.role = role;
        this.fechaNacimiento = fechaNacimiento;
        this.cui = cui;
        this.domicilio = domicilio;
        this.celular = celular;
        this.sexo = sexo;
        this.correo = correo;
        this.grado = grado;
        this.nivel = nivel;
        this.turno = turno;
        this.aula = aula;
        this.apoderadoNombre = apoderadoNombre;
        this.apoderadoCelular = apoderadoCelular;
    }

    // Constructor completo para empleado
    public UserInfoDTO(String nombres, String apellidoPaterno, String apellidoMaterno, 
                      String role, String fechaNacimiento, String cui, String domicilio,
                      String celular, String sexo, String correo, List<String> especialidades) {
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.role = role;
        this.fechaNacimiento = fechaNacimiento;
        this.cui = cui;
        this.domicilio = domicilio;
        this.celular = celular;
        this.sexo = sexo;
        this.correo = correo;
        this.especialidades = especialidades;
    }

    // Getters y Setters
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getApoderadoNombre() {
        return apoderadoNombre;
    }

    public void setApoderadoNombre(String apoderadoNombre) {
        this.apoderadoNombre = apoderadoNombre;
    }

    public String getApoderadoCelular() {
        return apoderadoCelular;
    }

    public void setApoderadoCelular(String apoderadoCelular) {
        this.apoderadoCelular = apoderadoCelular;
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
    }
}
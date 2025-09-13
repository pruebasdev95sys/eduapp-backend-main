package com.school.reportDto;

import java.io.Serializable;

import com.school.model.Estudiante;
import com.school.model.Matricula;

public class MatriculaEstudianteReporte implements Serializable {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String cui;
    private String fechaNacimiento;
    private String celular;
    private String correo;
    private String sexo;
    private String domicilio;
    private String grado;
    private String nivel;
    private String turno;
    private String aula;
    private String anioMatricula;
    private String colegioProcedencia;
    private String detalleMatricula;
    private String nombreApoderado;
    private String celularApoderado;
    private String username;
    private String password;
    private String mensajePassword; // Nuevo campo para mensaje informativo
    
    // Constructores
    public MatriculaEstudianteReporte() {}
    
    public MatriculaEstudianteReporte(Estudiante estudiante, Matricula matricula) {
        this.nombres = estudiante.getNombres();
        this.apellidoPaterno = estudiante.getApellidoPaterno();
        this.apellidoMaterno = estudiante.getApellidoMaterno();
        this.cui = estudiante.getCui();
        this.fechaNacimiento = estudiante.getFechaNacimiento();
        this.celular = estudiante.getCelular();
        this.correo = estudiante.getCorreo();
        this.sexo = estudiante.getSexo();
        this.domicilio = estudiante.getDomicilio();
        this.grado = estudiante.getGrado() != null ? estudiante.getGrado().getNombre() : "";
        this.nivel = estudiante.getNivel() != null ? estudiante.getNivel().getNombre() : "";
        this.turno = estudiante.getTurno() != null ? estudiante.getTurno().getNombre() : "";
        this.aula = estudiante.getAulaEstudiante() != null ? estudiante.getAulaEstudiante().getNombre() : "";
        
        if (matricula != null) {
            this.anioMatricula = matricula.getAnio();
            this.colegioProcedencia = matricula.getiEProcedencia();
            this.detalleMatricula = matricula.getDetalle();
        }
        
        if (estudiante.getApoderado() != null) {
            this.nombreApoderado = estudiante.getApoderado().getNombres() + " " + 
                                  estudiante.getApoderado().getApellidoPaterno() + " " + 
                                  estudiante.getApoderado().getApellidoMaterno();
            this.celularApoderado = estudiante.getApoderado().getCelular();
        }
        
        if (estudiante.getUsuario() != null) {
            this.username = estudiante.getUsuario().getUsername();
            
            // Siempre mostrar el CUI como contraseña inicial con mensaje informativo
            this.password = estudiante.getCui();
            this.mensajePassword = "Contraseña inicial (puede ser cambiada después del primer acceso)";
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
    
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    
    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }
    
    public String getAnioMatricula() { return anioMatricula; }
    public void setAnioMatricula(String anioMatricula) { this.anioMatricula = anioMatricula; }
    
    public String getColegioProcedencia() { return colegioProcedencia; }
    public void setColegioProcedencia(String colegioProcedencia) { this.colegioProcedencia = colegioProcedencia; }
    
    public String getDetalleMatricula() { return detalleMatricula; }
    public void setDetalleMatricula(String detalleMatricula) { this.detalleMatricula = detalleMatricula; }
    
    public String getNombreApoderado() { return nombreApoderado; }
    public void setNombreApoderado(String nombreApoderado) { this.nombreApoderado = nombreApoderado; }
    
    public String getCelularApoderado() { return celularApoderado; }
    public void setCelularApoderado(String celularApoderado) { this.celularApoderado = celularApoderado; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getMensajePassword() { return mensajePassword; }
    public void setMensajePassword(String mensajePassword) { this.mensajePassword = mensajePassword; }
    
    private static final long serialVersionUID = 1L;
}
package com.school.reportDto;

import java.io.Serializable;

public class AsistenciaEstudianteReporte implements Serializable {
    private String nombreCompletoEstudiante;
    private String grado;
    private String aula;
    private String curso;
    private String fecha;
    private String estadoAsistencia;
    
    // Constructores
    public AsistenciaEstudianteReporte() {}
    
    public AsistenciaEstudianteReporte(String nombreCompletoEstudiante, String grado, 
                                     String aula, String curso, String fecha, 
                                     String estadoAsistencia) {
        this.nombreCompletoEstudiante = nombreCompletoEstudiante;
        this.grado = grado;
        this.aula = aula;
        this.curso = curso;
        this.fecha = fecha;
        this.estadoAsistencia = estadoAsistencia;
    }
    
    // Getters y Setters
    public String getNombreCompletoEstudiante() {
        return nombreCompletoEstudiante;
    }

    public void setNombreCompletoEstudiante(String nombreCompletoEstudiante) {
        this.nombreCompletoEstudiante = nombreCompletoEstudiante;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }

    private static final long serialVersionUID = 1L;
}
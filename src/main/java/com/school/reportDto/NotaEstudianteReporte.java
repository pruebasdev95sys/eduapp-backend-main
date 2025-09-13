package com.school.reportDto;

import java.io.Serializable;

public class NotaEstudianteReporte implements Serializable {
	private String nombreCompletoEstudiante;
    private String grado;
    private String aula;
    private String nombreCurso;
    private Integer notaBim1;
    private Integer notaBim2;
    private Integer notaBim3;
    private Integer notaBim4;
    private Double promedioFinal;
    private String estado;
    
    // Constructores, getters y setters
    public NotaEstudianteReporte() {}
    
    public NotaEstudianteReporte(String nombreCompletoEstudiante,
            String grado, String aula, String nombreCurso,
            Integer notaBim1, Integer notaBim2, Integer notaBim3, Integer notaBim4, 
            Double promedioFinal) {
	this.nombreCompletoEstudiante = nombreCompletoEstudiante;
	this.grado = grado;
	this.aula = aula;
	this.nombreCurso = nombreCurso;
	this.notaBim1 = notaBim1;
	this.notaBim2 = notaBim2;
	this.notaBim3 = notaBim3;
	this.notaBim4 = notaBim4;
	this.promedioFinal = promedioFinal;
	this.estado = (promedioFinal != null && promedioFinal >= 60) ? "APROBADO" : "DESAPROBADO";
	}
    
    // Getters y setters para todos los campos
    
    
    private static final long serialVersionUID = 1L;


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

	public String getNombreCurso() {
		return nombreCurso;
	}

	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}

	public Integer getNotaBim1() {
		return notaBim1;
	}

	public void setNotaBim1(Integer notaBim1) {
		this.notaBim1 = notaBim1;
	}

	public Integer getNotaBim2() {
		return notaBim2;
	}

	public void setNotaBim2(Integer notaBim2) {
		this.notaBim2 = notaBim2;
	}

	public Integer getNotaBim3() {
		return notaBim3;
	}

	public void setNotaBim3(Integer notaBim3) {
		this.notaBim3 = notaBim3;
	}

	public Integer getNotaBim4() {
		return notaBim4;
	}

	public void setNotaBim4(Integer notaBim4) {
		this.notaBim4 = notaBim4;
	}

	public Double getPromedioFinal() {
		return promedioFinal;
	}

	public void setPromedioFinal(Double promedioFinal) {
		this.promedioFinal = promedioFinal;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
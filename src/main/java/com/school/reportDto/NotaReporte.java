package com.school.reportDto;

import java.io.Serializable;

public class NotaReporte implements Serializable  {
    private String nombresEstudiante;
    private String apellidosEstudiante;
    private Integer notaBim1;
    private Integer notaBim2;
    private Integer notaBim3;
    private Integer notaBim4;
    private Double promedioFinal;
    private String estado;
    private String nombreCurso;
    private String nombreAula;
    private String seccionAula;
    
    // Constructores, getters y setters
    public NotaReporte() {}
    
    public NotaReporte(String nombresEstudiante, String apellidosEstudiante, 
                      Integer notaBim1, Integer notaBim2, Integer notaBim3, Integer notaBim4, 
                      Double promedioFinal, String nombreCurso, String nombreAula, String seccionAula) {
        this.nombresEstudiante = nombresEstudiante;
        this.apellidosEstudiante = apellidosEstudiante;
        this.notaBim1 = notaBim1;
        this.notaBim2 = notaBim2;
        this.notaBim3 = notaBim3;
        this.notaBim4 = notaBim4;
        this.promedioFinal = promedioFinal;
        this.estado = (promedioFinal != null && promedioFinal >= 60) ? "APROBADO" : "DESAPROBADO";
        this.nombreCurso = nombreCurso;
        this.nombreAula = nombreAula;
        this.seccionAula = seccionAula;
    }
    
    
    
    public String getNombresEstudiante() {
		return nombresEstudiante;
	}

	public void setNombresEstudiante(String nombresEstudiante) {
		this.nombresEstudiante = nombresEstudiante;
	}

	public String getApellidosEstudiante() {
		return apellidosEstudiante;
	}

	public void setApellidosEstudiante(String apellidosEstudiante) {
		this.apellidosEstudiante = apellidosEstudiante;
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

	public String getNombreCurso() {
		return nombreCurso;
	}

	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}

	public String getNombreAula() {
		return nombreAula;
	}

	public void setNombreAula(String nombreAula) {
		this.nombreAula = nombreAula;
	}

	public String getSeccionAula() {
		return seccionAula;
	}

	public void setSeccionAula(String seccionAula) {
		this.seccionAula = seccionAula;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	private static final long serialVersionUID = 1L;
}

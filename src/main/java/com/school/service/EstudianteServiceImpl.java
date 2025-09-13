package com.school.service;

import java.util.List;
import java.util.Optional;

import com.school.model.Nota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.dao.EstudianteDao;
import com.school.model.Estudiante;
import com.school.model.Matricula;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;


import javax.annotation.PostConstruct;

import com.school.dao.MaterialDao;
import com.school.dao.NotaDao;
import com.school.model.Asignacion;
import com.school.model.Aula;
import com.school.model.Nota;
import com.school.reportDto.AsistenciaReporte;
import com.school.reportDto.CursoReporte;
import com.school.reportDto.MatriculaEstudianteReporte;
import com.school.reportDto.NotaEstudianteReporte;
import com.school.reportDto.NotaReporte;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;


import com.school.dao.AulaDao;
import com.school.dao.ClaseDao;
import com.school.model.Clase;
import com.school.model.Curso;


import net.sf.jasperreports.engine.*;


@Service
public class EstudianteServiceImpl implements EstudianteService{

	@Autowired
	private EstudianteDao estudianteDao;
	
    @Autowired
    private MatriculaService matriculaService;
    
    
	
	@Override
	@Transactional
	public Estudiante save(Estudiante estudiante) {
		return estudianteDao.save(estudiante);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Estudiante> saveAll(List<Estudiante> estudiantes) {

		estudiantes.stream()
				.forEach(e -> e.getAsistencias()
						.stream()
						.forEach(a-> { if(a.getEstudiante() == null) a.setEstudiante(e); }));

		return estudianteDao.saveAll(estudiantes);
	}

	@Override
	public Estudiante loginUsuario(String username, String password) {
		return estudianteDao.loginUsuario(username, password);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Estudiante> getEstudianteById(Long id) {
		return estudianteDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Estudiante> findAll() {
		return estudianteDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Estudiante> findAll(Pageable pageable) {
		return estudianteDao.findAll(pageable);
	}

	@Override
	@Transactional
	public boolean delete(Long id) {
		return getEstudianteById(id).map(estudiante -> {
			estudianteDao.deleteById(id);
			return true;
		}).orElse(false);
	}


	@Override
	public Estudiante findByDniAndDni(String username, String password) {
		return estudianteDao.findByCuiAndCui(username,password);
	}

	@Override
	public Estudiante findByDni(String dni) {
		return estudianteDao.findByCui(dni);
	}

	
	@Override
    public Optional<Estudiante> findByUsuarioId(Long usuarioId) {
        return Optional.ofNullable(estudianteDao.findByUsuario_Id(usuarioId));
    }
	
	@Override
	public Optional<Estudiante> findByCui(String cui) {
	    Estudiante estudiante = estudianteDao.findByCui(cui);
	    return Optional.ofNullable(estudiante);
	}
	
	@Override
	public List<NotaEstudianteReporte> getNotasReportePorEstudiante(String dni) {
	    Estudiante estudiante = estudianteDao.findByCui(dni);
	    List<NotaEstudianteReporte> reporte = new ArrayList<>();
	    
	    if (estudiante == null || estudiante.getNotas() == null || estudiante.getNotas().isEmpty()) {
	        return reporte;
	    }
	    
	    String nombreCompleto = estudiante.getNombres() + " " + 
                estudiante.getApellidoPaterno() + " " + 
                estudiante.getApellidoMaterno();
	    
	    for (Nota nota : estudiante.getNotas()) {
	        String nombreAula = "Sin aula asignada";
	        if (estudiante.getAulaEstudiante() != null) {
	            nombreAula = estudiante.getAulaEstudiante().getNombre() + " " + 
	                        (estudiante.getAulaEstudiante().getSeccion() != null ? 
	                         estudiante.getAulaEstudiante().getSeccion() : "");
	        }
	        
	        String nombreGrado = "Sin grado asignado";
	        if (estudiante.getGrado() != null) {
	            nombreGrado = estudiante.getGrado().getNombre();
	        }
	        
	        NotaEstudianteReporte notaReporte = new NotaEstudianteReporte(
	        	nombreCompleto,
	            nombreGrado,
	            nombreAula,
	            nota.getCurso().getNombre(),
	            nota.getNota_bim1(),
	            nota.getNota_bim2(),
	            nota.getNota_bim3(),
	            nota.getNota_bim4(),
	            nota.getPromedio_final()
	        );
	        
	        reporte.add(notaReporte);
	    }
	    
	    return reporte;
	}

	@Override
	public byte[] generarReporteNotasEstudiante(String tipo, String dni) {
	    byte[] data = null;
	    List<NotaEstudianteReporte> notasReporte = getNotasReportePorEstudiante(dni);
	    
	    if (notasReporte == null || notasReporte.isEmpty()) return data;
	    
	    try {
	        // Cargar el reporte desde resources
	        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteNotasPorEstudiante.jasper");
	        
	        if (reportStream == null) {
	            throw new RuntimeException("Archivo reporteNotasPorEstudiante.jasper no encontrado en resources");
	        }
	        
	        // Parámetros para el reporte
	        Map<String, Object> parameters = new HashMap<>();
	        parameters.put("estudianteNombreCompleto", notasReporte.get(0).getNombreCompletoEstudiante());
	        parameters.put("estudianteGrado", notasReporte.get(0).getGrado());
	        parameters.put("estudianteAula", notasReporte.get(0).getAula());
	        
	        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(notasReporte));
	        
	        if ("pdf".equals(tipo)) {
	            data = JasperExportManager.exportReportToPdf(jasperPrint);
	        } else {
	            SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
	            config.setOnePagePerSheet(true);
	            config.setIgnoreGraphics(false);
	            
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            Exporter exporter = new JRXlsxExporter();
	            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
	            exporter.exportReport();
	            
	            data = out.toByteArray();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error al generar reporte de notas por estudiante: " + e.getMessage(), e);
	    }
	    
	    return data;
	}
	
	@Override
    public MatriculaEstudianteReporte getDatosReporteMatricula(Long estudianteId) {
        Optional<Estudiante> estudianteOpt = estudianteDao.findById(estudianteId);
        if (!estudianteOpt.isPresent()) {
            return null;
        }
        
        Estudiante estudiante = estudianteOpt.get();
        List<Matricula> matriculas = matriculaService.getMatriculasPorEstudiante(estudianteId);
        Matricula matricula = matriculas != null && !matriculas.isEmpty() ? matriculas.get(0) : null;
        
        // Usa el constructor sin PasswordEncoder
        return new MatriculaEstudianteReporte(estudiante, matricula);
    }
    
    @Override
    public byte[] generarReporteMatriculaEstudiante(String tipo, Long estudianteId) {
        byte[] data = null;
        MatriculaEstudianteReporte datosReporte = getDatosReporteMatricula(estudianteId);
        
        if (datosReporte == null) return data;
        
        try {
            // Cargar el reporte desde resources
            InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteMatriculaEstudiante.jasper");
            
            if (reportStream == null) {
                throw new RuntimeException("Archivo reporteMatriculaEstudiante.jasper no encontrado en resources");
            }
            
            // Convertir el objeto a una lista para el datasource
            List<MatriculaEstudianteReporte> datos = Arrays.asList(datosReporte);
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), new JRBeanCollectionDataSource(datos));
            
            if ("pdf".equals(tipo)) {
                data = JasperExportManager.exportReportToPdf(jasperPrint);
            } else {
                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(true);
                config.setIgnoreGraphics(false);
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
                
                data = out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar reporte de matrícula del estudiante: " + e.getMessage(), e);
        }
        
        return data;
    }

}

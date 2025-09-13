package com.school.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.school.dao.MaterialDao;
import com.school.dao.NotaDao;
import com.school.model.Asignacion;
import com.school.model.Asistencia;
import com.school.model.Aula;
import com.school.model.Nota;
import com.school.reportDto.AsistenciaEstudianteReporte;
import com.school.reportDto.AsistenciaReporte;
import com.school.reportDto.CursoReporte;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.dao.AsistenciaDao;
import com.school.dao.AulaDao;
import com.school.dao.ClaseDao;
import com.school.model.Clase;
import com.school.model.Curso;
import com.school.model.Estudiante;

import net.sf.jasperreports.engine.*;


@Service
public class ClaseServiceImpl implements ClaseService{

	@Autowired
	private ClaseDao claseDao;
	
	@Autowired
	private AulaDao aulaDao;

	@Autowired
	private NotaDao notaDao;
	
    @Autowired
    private AsistenciaDao asistenciaDao;

	@Override
	@Transactional
	public Clase save(Clase clase) {
		// TODO Auto-generated method stub
		clase.setAula(aulaDao.findById(clase.getAula().getId()).get());
		clase.getFrecuencias().stream().forEach(f -> f.setClase(clase));
		return claseDao.save(clase);
	}

	@Override
	public Clase saveNoFrecuenciaUpdate(Clase clase) {
		return claseDao.save(clase);
	}


	@Override
	@Transactional
	public Clase update(Clase clase) {
		// TODO Auto-generated method stub
		clase.getFrecuencias().stream().forEach(f -> f.setClase(clase));
		return claseDao.save(clase);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Clase> getClaseById(Long id) {
		// TODO Auto-generated method stub
		return claseDao.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Clase> findAll() {
		// TODO Auto-generated method stub
		return (List<Clase>) claseDao.findAll();
	}

	@Override
	@Transactional
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return getClaseById(id).map(c -> {
			claseDao.deleteById(id);
			return true;
		}).orElse(false);
	}

	@Override
	public byte[] generarReporteCurso(String tipo, Long idCurso, Long idGrado) {
	    byte[] data = null;

	    List<CursoReporte> listacursoReporte = getCursoReporte(idCurso, idGrado);

	    if(listacursoReporte == null || listacursoReporte.isEmpty()) return data;

	    try {
	        // SOLUCIÓN: Cargar desde InputStream en lugar de File
	        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteCursosAprobados.jasper");
	        
	        if (reportStream == null) {
	            throw new RuntimeException("Archivo reporteCursosAprobados.jasper no encontrado en resources");
	        }

	        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
	        JasperPrint rpt = JasperFillManager.fillReport(jasperReport, null, new JRBeanCollectionDataSource(listacursoReporte));

	        if(tipo.equals("pdf")){
	            data = JasperExportManager.exportReportToPdf(rpt);
	        } else {
	            SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
	            config.setOnePagePerSheet(true);
	            config.setIgnoreGraphics(false);

	            ByteArrayOutputStream out = new ByteArrayOutputStream();

	            Exporter exporter = new JRXlsxExporter();
	            exporter.setExporterInput(new SimpleExporterInput(rpt));
	            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
	            exporter.exportReport();

	            data = out.toByteArray();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error al generar reporte: " + e.getMessage(), e);
	    }
	    return data;
	}


	@Override
	public List<CursoReporte> getCursoReporte(Long idCurso, Long idGrado) {

		List<Aula> aulasPorGrado = null;
		List<Aula> aulasGeneral = null;
		List<CursoReporte> cursoReporteList = new ArrayList<>();

		if(idGrado == 0){
			aulasGeneral = (List<Aula>) aulaDao.findAll();

			cursoReporteList = buscarAprobadosYDesaprobados(aulasGeneral, idCurso);

			return cursoReporteList;

		}
		aulasPorGrado = aulaDao.findAulaPorGrado(idGrado);
		cursoReporteList = buscarAprobadosYDesaprobados(aulasPorGrado, idCurso);


		return cursoReporteList;
	}

	@Override
	public List<Asignacion> asignacionesPorClase(Long idClase) {
		return claseDao.asignacionesPorClase(idClase);
	}

	private List<CursoReporte> buscarAprobadosYDesaprobados(List<Aula> aulas, Long idCurso){
		List<CursoReporte> cursoReporteList = new ArrayList<>();

		for(Aula aula: aulas){
			long aprobados = 0;
			long desaprobados = 0;
			Clase clase = claseDao.findClasePorAulaYCurso(idCurso ,aula.getId());
			if(clase != null){
				List<Nota> notas = notaDao.notasPorAulaYCurso(idCurso, aula.getId());

				if(notas.size() == 0){
					return cursoReporteList;
				}
				aprobados = notas.stream().filter(n -> n.getNota_bim1() > 12).count();
				desaprobados = notas.size() - aprobados;
				CursoReporte cursoReporte = new CursoReporte(notas.get(0).getEstudiante().getAulaEstudiante().getNombre() + notas.get(0).getEstudiante().getAulaEstudiante().getSeccion(),notas.get(0).getCurso().getNombre() , aprobados, desaprobados);
				cursoReporteList.add(cursoReporte);

			}
		}

		return cursoReporteList;
	}
	
	
	@Override
	public List<NotaReporte> getNotasReporte(Long idCurso, Long idAula) {
	    List<Nota> notas = notaDao.notasPorAulaYCurso(idCurso, idAula);
	    List<NotaReporte> reporte = new ArrayList<>();
	    
	    for (Nota nota : notas) {
	        Estudiante estudiante = nota.getEstudiante();
	        Aula aula = estudiante.getAulaEstudiante();
	        Curso curso = nota.getCurso();
	        
	        NotaReporte notaReporte = new NotaReporte(
	            estudiante.getNombres(),
	            estudiante.getApellidoPaterno() + " " + estudiante.getApellidoMaterno(),
	            nota.getNota_bim1(),
	            nota.getNota_bim2(),
	            nota.getNota_bim3(),
	            nota.getNota_bim4(),
	            nota.getPromedio_final(),
	            curso.getNombre(),
	            aula.getNombre(),
	            aula.getSeccion()
	        );
	        
	        reporte.add(notaReporte);
	    }
	    
	    return reporte;
	}

	@Override
	public byte[] generarReporteNotas(String tipo, Long idCurso, Long idAula) {
	    byte[] data = null;
	    List<NotaReporte> notasReporte = getNotasReporte(idCurso, idAula);
	    
	    if (notasReporte == null || notasReporte.isEmpty()) return data;
	    
	    try {
	        // Cargar el reporte desde resources
	        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteNotasEstudiantes.jasper");
	        
	        if (reportStream == null) {
	            throw new RuntimeException("Archivo reporteNotasEstudiantes.jasper no encontrado en resources");
	        }
	        
	        // Parámetros para el reporte
	        Map<String, Object> parameters = new HashMap<>();
	        parameters.put("cursoId", idCurso.toString());
	        parameters.put("aulaId", idAula.toString());
	        
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
	        throw new RuntimeException("Error al generar reporte de notas: " + e.getMessage(), e);
	    }
	    
	    return data;
	}
	
	   @Override
	    @Transactional(readOnly = true)
	    public List<AsistenciaEstudianteReporte> getAsistenciasReportePorCursoYFecha(Long idCurso, Long idAula, String fecha) {
	        List<Asistencia> asistencias = asistenciaDao.findAsistenciaByFechaAula(fecha, idAula);
	        List<AsistenciaEstudianteReporte> reporte = new ArrayList<>();
	        
	        for (Asistencia asistencia : asistencias) {
	            Estudiante estudiante = asistencia.getEstudiante();
	            
	            // Verificar si el estudiante está matriculado en el curso
	            boolean estaMatriculado = estudiante.getNotas().stream()
	                .anyMatch(nota -> nota.getCurso().getId().equals(idCurso));
	            
	            if (estaMatriculado) {
	                String nombreCompleto = estudiante.getNombres() + " " + 
	                    estudiante.getApellidoPaterno() + " " + 
	                    estudiante.getApellidoMaterno();
	                
	                String nombreGrado = "Sin grado asignado";
	                if (estudiante.getGrado() != null) {
	                    nombreGrado = estudiante.getGrado().getNombre();
	                }
	                
	                String nombreAula = "Sin aula asignada";
	                if (estudiante.getAulaEstudiante() != null) {
	                    nombreAula = estudiante.getAulaEstudiante().getNombre() + " " + 
	                        (estudiante.getAulaEstudiante().getSeccion() != null ? 
	                         estudiante.getAulaEstudiante().getSeccion() : "");
	                }
	                
	                String nombreCurso = "Curso no encontrado";
	                Optional<Curso> cursoOpt = estudiante.getNotas().stream()
	                    .filter(nota -> nota.getCurso().getId().equals(idCurso))
	                    .map(Nota::getCurso)
	                    .findFirst();
	                
	                if (cursoOpt.isPresent()) {
	                    nombreCurso = cursoOpt.get().getNombre();
	                }
	                
	                AsistenciaEstudianteReporte asistenciaReporte = new AsistenciaEstudianteReporte(
	                    nombreCompleto,
	                    nombreGrado,
	                    nombreAula,
	                    nombreCurso,
	                    asistencia.getFecha(),
	                    asistencia.getEstado()
	                );
	                
	                reporte.add(asistenciaReporte);
	            }
	        }
	        
	        return reporte;
	    }
	    
	    @Override
	    public byte[] generarReporteAsistenciaPorCurso(String tipo, Long idCurso, Long idAula, String fecha) {
	        byte[] data = null;
	        List<AsistenciaEstudianteReporte> asistenciasReporte = getAsistenciasReportePorCursoYFecha(idCurso, idAula, fecha);
	        
	        if (asistenciasReporte == null || asistenciasReporte.isEmpty()) return data;
	        
	        try {
	            // Cargar el reporte desde resources
	            InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteAsistenciaPorCurso.jasper");
	            
	            if (reportStream == null) {
	                throw new RuntimeException("Archivo reporteAsistenciaPorCurso.jasper no encontrado en resources");
	            }
	            
	            // Parámetros para el reporte
	            Map<String, Object> parameters = new HashMap<>();
	            parameters.put("fecha", fecha);
	            parameters.put("curso", asistenciasReporte.get(0).getCurso());
	            parameters.put("aula", asistenciasReporte.get(0).getAula());
	            parameters.put("grado", asistenciasReporte.get(0).getGrado());
	            
	            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
	            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(asistenciasReporte));
	            
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
	            throw new RuntimeException("Error al generar reporte de asistencias por curso: " + e.getMessage(), e);
	        }
	        
	        return data;
	    }

}

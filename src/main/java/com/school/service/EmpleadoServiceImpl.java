package com.school.service;

import com.school.dao.EmpleadoDao;
import com.school.model.Clase;
import com.school.model.Empleado;
import com.school.reportDto.ReporteEmpleados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import java.io.ByteArrayOutputStream;

import java.util.HashMap;


import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;



@Service
public class EmpleadoServiceImpl implements EmpleadoService{

    @Autowired
    private EmpleadoDao empleadoDao;



    @Override
    public Empleado save(Empleado empleado) {
        return empleadoDao.save(empleado);
    }

    @Override
    public Optional<Empleado> getEmpleadoById(Long id) {
        return empleadoDao.findById(id);
    }

    @Override
    public List<Empleado> findAll() {
        return empleadoDao.findAll();
    }

    @Override
    public Page<Empleado> findAll(Pageable pageable) {
        return empleadoDao.findAll(pageable);
    }

    @Override
    public boolean delete(Long id) {
        return getEmpleadoById(id).map(e -> {
            empleadoDao.deleteById(id);
            return true;
        }).orElse(false);
    }

    @Override
    public List<Clase> findClasesProfesor(Long id) {
        return empleadoDao.findClasesProfesor(id);
    }

    @Override
    public Empleado findByDni(String dni) {
        return empleadoDao.findByCui(dni);
    }
    
    @Override
    public Optional<Empleado> findByUsuarioId(Long usuarioId) {
        return Optional.ofNullable(empleadoDao.findByUsuario_Id(usuarioId));
    }
    
    
    @Override
    public Optional<Empleado> findByCui(String cui) {
        Empleado empleado = empleadoDao.findByCui(cui);
        return Optional.ofNullable(empleado);
    }
    
    @Override
    public ReporteEmpleados getDatosReporteEmpleado(Long empleadoId) {
        Optional<Empleado> empleadoOpt = empleadoDao.findById(empleadoId);
        if (!empleadoOpt.isPresent()) {
            return null;
        }
        
        Empleado empleado = empleadoOpt.get();
        return new ReporteEmpleados(empleado);
    }
    
    @Override
    public byte[] generarReporteEmpleado(String tipo, Long empleadoId) {
        byte[] data = null;
        ReporteEmpleados datosReporte = getDatosReporteEmpleado(empleadoId);
        
        if (datosReporte == null) return data;
        
        try {
            // Cargar el reporte desde resources
            InputStream reportStream = getClass().getClassLoader().getResourceAsStream("reporteEmpleados.jasper");
            
            if (reportStream == null) {
                throw new RuntimeException("Archivo reporteEmpleados.jasper no encontrado en resources");
            }
            
            // Convertir el objeto a una lista para el datasource
            List<ReporteEmpleados> datos = Arrays.asList(datosReporte);
            
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
            throw new RuntimeException("Error al generar reporte del empleado: " + e.getMessage(), e);
        }      
        return data;
    }
}

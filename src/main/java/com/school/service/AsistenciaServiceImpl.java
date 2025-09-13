package com.school.service;

import com.school.dao.AsistenciaDao;
import com.school.model.Asistencia;
import com.school.reportDto.AsistenciaReporte;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsistenciaServiceImpl implements AsistenciaService{

    @Autowired
    private AsistenciaDao asistenciaDao;

    @Autowired
    private ClaseService claseService;



    @Override
    public byte[] generarReporteAsistencia(String tipo, String fecha) {
        byte[] data = null;

        AsistenciaReporte asistenciaDTO = obtenerDatosAsistenciaPorDia(fecha);

        if(asistenciaDTO == null) {
            throw new RuntimeException("No hay datos de asistencia para la fecha: " + fecha);
        }

        List<AsistenciaReporte> lista = new ArrayList<>();
        lista.add(asistenciaDTO);

        try {
            // SOLUCIÓN: Cargar el archivo .jasper como InputStream
            InputStream jasperStream = getClass().getClassLoader().getResourceAsStream("reporteAsistenciaEstadistica.jasper");
            
            if (jasperStream == null) {
                throw new RuntimeException("Archivo reporteAsistenciaEstadistica.jasper no encontrado en resources");
            }

            // Cargar el reporte compilado
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            
            // Crear parámetros si son necesarios
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_PARAM", fecha);

            JasperPrint rpt = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(lista));

            if("pdf".equals(tipo)){
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
    public List<Asistencia> findByFecha(String fecha) {
        return asistenciaDao.findByFecha(fecha);
    }

    @Override
    public List<Asistencia> findAsistenciaByFechaAula(String fecha, Long idAula) {
        return asistenciaDao.findAsistenciaByFechaAula(fecha, idAula);
    }

    @Override
    public AsistenciaReporte obtenerDatosAsistenciaPorDia(String fecha){

        List<Asistencia> asistencias = findByFecha(fecha);

        if(asistencias.size() == 0) return null;

        Long puntual = 0L;
        Long tardanza = 0L;
        Long inasistencia = 0L;

        for(Asistencia asistencia: asistencias){
            if(asistencia.getEstado().equals("PUNTUAL")) puntual++;
            if(asistencia.getEstado().equals("TARDANZA")) tardanza++;
            if(asistencia.getEstado().equals("FALTA")) inasistencia++;
        }

        AsistenciaReporte asistenciaDTO = new AsistenciaReporte(fecha,puntual,tardanza,inasistencia);

        return asistenciaDTO;
    }

    @Override
    public List<Asistencia> updateAsistencias(List<Asistencia> asistencias) {
        return asistenciaDao.saveAll(asistencias);
    }


}

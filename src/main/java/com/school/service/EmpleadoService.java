package com.school.service;

import com.school.model.Clase;
import com.school.model.Empleado;
import com.school.model.Estudiante;
import com.school.reportDto.ReporteEmpleados;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmpleadoService {

    public Empleado save(Empleado empleado);


    public Optional<Empleado> getEmpleadoById(Long id);

    public List<Empleado> findAll();

    public Page<Empleado> findAll(Pageable pageable);

    public boolean delete(Long id);

    public List<Clase> findClasesProfesor(Long id);

    public Empleado findByDni(String dni);
    
    //Empleado findByUsuarioId(Long usuarioId);
 // Nuevo método
    Optional<Empleado> findByUsuarioId(Long usuarioId);
    
    Optional<Empleado> findByCui(String cui);
    
    // Nuevos métodos para el reporte de empleados
    ReporteEmpleados getDatosReporteEmpleado(Long empleadoId);
    byte[] generarReporteEmpleado(String tipo, Long empleadoId);

}

package com.school.controller;

import com.school.model.*;
import com.school.service.AsignacionService;
import com.school.service.EstudianteService;
import com.school.service.RespuestaAsignacionService;
import com.school.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private RespuestaAsignacionService respuestaAsignacionService;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private EstudianteService estudianteService;

    @PostMapping("/crear/{idClase}")
    public ResponseEntity<?> crearAsignacion(@Valid @RequestBody Asignacion asignacion, 
                                           BindingResult results, 
                                           @PathVariable Long idClase) {

        Map<String, Object> response = new HashMap<>();

        // Validación de campos
        if(results.hasErrors()) {
            List<String> errors = results.getFieldErrors()
                    .stream()
                    .map(er -> {
                        // Manejo especial para campos específicos
                        switch(er.getField()) {
                            case "puntuacionMaxima":
                                return "La puntuación máxima " + er.getDefaultMessage();
                            case "fechaInicio":
                            case "fechaFin":
                                return "La fecha " + er.getDefaultMessage();
                            default:
                                return "El campo '" + er.getField() + "' " + er.getDefaultMessage();
                        }
                    })
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Validación adicional de fechas
        if(asignacion.getFechaInicio() != null && asignacion.getFechaFin() != null) {
            if(asignacion.getFechaFin().isBefore(asignacion.getFechaInicio())) {
                response.put("mensaje", "La fecha de finalización debe ser posterior a la fecha de inicio");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if(asignacion.getFechaInicio().isBefore(LocalDateTime.now())) {
                response.put("mensaje", "La fecha de inicio no puede ser en el pasado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // Validación de puntuación máxima
        if(asignacion.getPuntuacionMaxima() == null || asignacion.getPuntuacionMaxima() <= 0) {
            response.put("mensaje", "La puntuación máxima debe ser un número positivo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Guardar la asignación
            Asignacion asignacionNueva = asignacionService.save(asignacion, idClase);
            
            // Preparar respuesta exitosa
            response.put("mensaje", "La asignación ha sido creada con éxito!");
            response.put("asignacion", asignacionNueva);
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (DataAccessException e) {
            // Manejo de errores de base de datos
            String errorMsg = "Error al insertar la asignación en la base de datos";
            if(e.getMostSpecificCause() != null) {
                errorMsg += ": " + e.getMostSpecificCause().getMessage();
            }
            
            response.put("mensaje", errorMsg);
            response.put("error", e.getMessage());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            
        } catch (IllegalArgumentException e) {
            // Manejo de errores de argumentos inválidos
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            // Manejo de otros errores inesperados
            response.put("mensaje", "Ocurrió un error inesperado al crear la asignación");
            response.put("error", e.getMessage());
            
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/actualizar/{idAsignacion}")
    public ResponseEntity<?> actualizarAsignacion(
            @Valid @RequestBody Asignacion asignacion,
            BindingResult results,
            @PathVariable Long idAsignacion) {

        Map<String, Object> response = new HashMap<>();

        // 1. Validar errores de binding
        if (results.hasErrors()) {
            List<String> errors = results.getFieldErrors()
                    .stream()
                    .map(er -> {
                        switch (er.getField()) {
                            case "puntuacionMaxima":
                                return "La puntuación máxima " + er.getDefaultMessage();
                            case "fechaInicio":
                            case "fechaFin":
                                return "La fecha " + er.getDefaultMessage();
                            default:
                                return "El campo '" + er.getField() + "' " + er.getDefaultMessage();
                        }
                    })
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 2. Validar que exista la asignación
        Asignacion asignacionExistente = asignacionService.getAsignacionoById(idAsignacion)
                .orElse(null);

        if (asignacionExistente == null) {
            response.put("mensaje", "La asignación con ID " + idAsignacion + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // 3. Validaciones adicionales de fechas
        if (asignacion.getFechaInicio() != null && asignacion.getFechaFin() != null) {
            // 3.1 Validar que fechaFin sea posterior a fechaInicio
            if (asignacion.getFechaFin().isBefore(asignacion.getFechaInicio())) {
                response.put("mensaje", "La fecha de finalización debe ser posterior a la fecha de inicio");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 3.2 Validar que fechaInicio no sea en el pasado (solo si se modificó)
            if (!asignacionExistente.getFechaInicio().isEqual(asignacion.getFechaInicio()) &&
                asignacion.getFechaInicio().isBefore(LocalDateTime.now())) {
                response.put("mensaje", "No se puede cambiar la fecha de inicio a una fecha pasada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // 4. Validación de puntuación máxima
        if (asignacion.getPuntuacionMaxima() == null || asignacion.getPuntuacionMaxima() <= 0) {
            response.put("mensaje", "La puntuación máxima debe ser un número positivo");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // 5. Actualizar solo los campos permitidos
            asignacionExistente.setTitulo(asignacion.getTitulo());
            asignacionExistente.setDescripcion(asignacion.getDescripcion());
            asignacionExistente.setPuntuacionMaxima(asignacion.getPuntuacionMaxima());
            
            // Solo actualizar fechas si son diferentes
            if (!asignacionExistente.getFechaInicio().isEqual(asignacion.getFechaInicio())) {
                asignacionExistente.setFechaInicio(asignacion.getFechaInicio());
            }
            
            if (!asignacionExistente.getFechaFin().isEqual(asignacion.getFechaFin())) {
                asignacionExistente.setFechaFin(asignacion.getFechaFin());
            }

            // 6. Guardar cambios
            Asignacion asignacionActualizada = asignacionService.update(asignacionExistente);

            // 7. Preparar respuesta exitosa
            response.put("mensaje", "La asignación ha sido actualizada con éxito");
            response.put("asignacion", asignacionActualizada);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (DataAccessException e) {
            // 8. Manejo de errores de base de datos
            response.put("mensaje", "Error al actualizar la asignación en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(
                    e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            
        } catch (Exception e) {
            // 9. Manejo de otros errores
            response.put("mensaje", "Error inesperado al actualizar la asignación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAsignacion(@PathVariable Long id) {
        Optional<Asignacion> asignacion = null;
        Map<String, Object> response = new HashMap<>();

        try {
            asignacion = asignacionService.getAsignacionoById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la asignación en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(asignacion.isEmpty()) {
            response.put("mensaje", "La asignación con el ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("asignacion", asignacion.get());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsignacion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        List<RespuestaAsignacion> respuestas = respuestaAsignacionService.findAllByAsignacion(id);

        try {
            if(!respuestas.isEmpty()){
                respuestas.forEach(r -> {
                    uploadFileService.deleteFile(r.getArchivo());
                    respuestaAsignacionService.delete(r.getId());
                });
            }

            asignacionService.deleteById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la asignación en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("eliminado", "La asignación con el id: "+id+" se eliminó con éxito.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/respuestas/{idAsignacion}")
    public ResponseEntity<List<RespuestaAsignacion>> listaRespuestasPorAsignacion(@PathVariable Long idAsignacion) {
        return new ResponseEntity<>(respuestaAsignacionService.findAllByAsignacion(idAsignacion), HttpStatus.OK);
    }

    @PutMapping("/respuestas/{idRespuesta}")
    public ResponseEntity<?> actualizarRespuestaAsignacion(
            @RequestParam("nota") String nota, 
            @PathVariable Long idRespuesta) {

        Map<String, Object> response = new HashMap<>();
        RespuestaAsignacion respuesta = respuestaAsignacionService.findById(idRespuesta).orElse(null);
        
        if(respuesta == null) {
            response.put("mensaje", "La respuesta con el id: " + idRespuesta + " no existe en la BBDD.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Validar que la asignación esté activa
        if(!respuesta.getAsignacion().getActivo()) {
            response.put("mensaje", "No se puede calificar una asignación inactiva");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Convertir y validar la nota
            double notaValue = Double.parseDouble(nota);
            int puntuacionMaxima = respuesta.getAsignacion().getPuntuacionMaxima();
            
            if(notaValue < 0) {
                response.put("mensaje", "La nota no puede ser negativa");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            if(notaValue > puntuacionMaxima) {
                response.put("mensaje", "La nota no puede exceder la puntuación máxima de " + puntuacionMaxima);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            respuesta.setNota(nota);
            RespuestaAsignacion respuestaActualizada = respuestaAsignacionService.update(respuesta);
            
            response.put("mensaje", "Nota actualizada correctamente");
            response.put("respuesta", respuestaActualizada);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (NumberFormatException e) {
            response.put("mensaje", "La nota debe ser un valor numérico válido");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar la respuesta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/respuestas/crear")
    public ResponseEntity<?> crearRespuestaAsignacion(
            @RequestParam("idAsignacion") String idAsignacion, 
            @RequestParam("archivo") MultipartFile archivo, 
            @RequestParam("dniEstudiante") String dniEstudiante) {

        RespuestaAsignacion respuestaAsignacionNueva = null;
        Map<String, Object> response = new HashMap<>();

        if(!archivo.isEmpty()){
            String nombreArchivo = null;
            RespuestaAsignacion respuestaAsignacion = new RespuestaAsignacion(); // Aquí se setea automáticamente la fecha
            Estudiante estudiante = null;
            
            try {
                estudiante = estudianteService.findByDni(dniEstudiante);
                if(estudiante == null) {
                    response.put("mensaje", "El estudiante con DNI " + dniEstudiante + " no existe");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }

                Asignacion asignacion = asignacionService.getAsignacionoById(Long.parseLong(idAsignacion))
                    .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
                
                if(!asignacion.getActivo()) {
                    response.put("mensaje", "No se puede enviar respuestas a una asignación inactiva");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                nombreArchivo = uploadFileService.uploadFile(archivo);
                respuestaAsignacion.setArchivo(nombreArchivo);
                respuestaAsignacion.setNombresEstudiante(
                    estudiante.getNombres() + " " + 
                    estudiante.getApellidoPaterno() + " " + 
                    estudiante.getApellidoMaterno());
                respuestaAsignacion.setDniEstudiante(dniEstudiante);
                
                // La fecha ya se setea automáticamente en el constructor
                respuestaAsignacionNueva = respuestaAsignacionService.save(respuestaAsignacion, Long.parseLong(idAsignacion));
                
            } catch (IOException e) {
                response.put("mensaje", "Error al subir archivo.");
                response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (RuntimeException e) {
                response.put("mensaje", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        }

        response.put("mensaje", "La respuesta ha sido creada con éxito!");
        response.put("respuestaAsignacion", respuestaAsignacionNueva);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/respuestas/buscarRespuesta")
    public ResponseEntity<?> buscarRespuestaEstudiante(
            @RequestParam("dniEstudiante") String dniEstudiante, 
            @RequestParam("idAsignacion") String idAsignacion) {

        Optional<RespuestaAsignacion> respuestaAsignacion = respuestaAsignacionService
            .findPorDniEstudianteAndAsignacion(dniEstudiante, Long.parseLong(idAsignacion));
        
        if(respuestaAsignacion.isEmpty()) {
            return new ResponseEntity<>("No se encontró la respuesta", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(respuestaAsignacion.get(), HttpStatus.OK);
    }

    @DeleteMapping("/respuestas/{idRespuesta}")
    public ResponseEntity<?> eliminarRespuesta(@PathVariable Long idRespuesta) {
        RespuestaAsignacion respuestaEncontrada = respuestaAsignacionService.findById(idRespuesta).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if(respuestaEncontrada != null) {
            try {
                respuestaAsignacionService.delete(idRespuesta);
                uploadFileService.deleteFile(respuestaEncontrada.getArchivo());
                response.put("eliminado", true);
                response.put("mensaje", "Respuesta eliminada correctamente");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (DataAccessException e) {
                response.put("mensaje", "Error al eliminar la respuesta");
                response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response.put("eliminado", false);
            response.put("mensaje", "La respuesta no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
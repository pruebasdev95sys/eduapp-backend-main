package com.school.security.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.school.model.Empleado;
import com.school.model.Estudiante;
import com.school.security.dto.CambiarContrasenaDTO;
import com.school.security.dto.CambiarContrasenaLoginDTO;
import com.school.security.dto.JwtDto;
import com.school.security.dto.LoginUsuario;
import com.school.security.dto.NuevoUsuario;
import com.school.security.dto.UserInfoDTO;
import com.school.security.enums.RolNombre;
import com.school.security.jwt.JwtProvider;
import com.school.security.models.Rol;
import com.school.security.models.Usuario;
import com.school.security.service.IPasswordService;
import com.school.security.service.IRolService;
import com.school.security.service.IUsuarioService;
import com.school.service.EmpleadoService;
import com.school.service.EstudianteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class AuthController {

	@Autowired
	private PasswordEncoder passwordEnconder;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IRolService rolService;
	
	@Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private IPasswordService passwordService;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@PutMapping("/usuarios/{id}")
	public ResponseEntity<?> actualizarUsuario(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id){
		Usuario usuarioActual = usuarioService.findById(id);
		Usuario usuarioActualizado = null;
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(usuarioActual == null) {
			response.put("mensaje", "Error: No se pudo editar el usuario con el ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			usuarioActual.setEnabled(usuario.getEnabled());
			usuarioActual.setPassword(usuario.getPassword());
			
			usuarioActualizado = usuarioService.save(usuarioActual);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el usuario en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Usuario actualizado con éxito!");
		response.put("usuario", usuarioActualizado);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/nuevo")
	public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult result){
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(usuarioService.existsByUsername(nuevoUsuario.getUsername())) {
			response.put("mensaje", "El usuario ya existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);

		}

		Usuario usuario = new Usuario(nuevoUsuario.getUsername(), passwordEnconder.encode(nuevoUsuario.getPassword()), nuevoUsuario.getEnabled());

		Set<Rol> roles = new HashSet<>();
		if(nuevoUsuario.getRoles().contains("ROLE_ESTUDIANTE")){
			roles.add(rolService.findByRolNombre(RolNombre.ROLE_ESTUDIANTE).get());
		}

		if(nuevoUsuario.getRoles().contains("ROLE_PROFESOR")){
			roles.add(rolService.findByRolNombre(RolNombre.ROLE_PROFESOR).get());
		}

		if(nuevoUsuario.getRoles().contains("ROLE_ADMIN")){
			roles.add(rolService.findByRolNombre(RolNombre.ROLE_ADMIN).get());
		}
		
		if(nuevoUsuario.getRoles().contains("admin"))
			roles.add(rolService.findByRolNombre(RolNombre.ROLE_ADMIN).get());
		
		usuario.setRoles(roles);
		
		Usuario usuarioCreado = usuarioService.save(usuario);
		
		response.put("mensaje", "Usuario guardado");
		response.put("usuario", usuarioCreado);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult result){
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		
		Authentication auth = authManager
				.authenticate(
						new UsernamePasswordAuthenticationToken(loginUsuario.getUsername(), loginUsuario.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		String jwt = jwtProvider.generatToken(auth);
		
		JwtDto jwtDto = new JwtDto(jwt);
		
		response.put("mensaje", "Login exitoso");
		response.put("jwtDto", jwtDto);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/usuarios/{username}")
	public ResponseEntity<?> findUsuario(@PathVariable String username){
		
		Usuario usuario = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			usuario = usuarioService.findByUsername(username).get();
		}catch(DataAccessException e) {//Por si hay un error en la BBDD
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(usuario == null) {
			response.put("mensaje", "El usuario: ".concat(usuario.toString().concat(" no existe en la base de datos")));
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException{
		String token = jwtProvider.refreshToken(jwtDto);
		JwtDto jwt = new JwtDto(token);
		
		return new ResponseEntity<JwtDto>(jwt, HttpStatus.OK);
	}
	
	
	// Endpoint para obtener la info del usuario autenticado
	@GetMapping("/userinfo")
	public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
		Map<String, Object> response = new HashMap<>();

		try {
			String jwt = token.substring(7);
			String username = jwtProvider.getUsernameFromToken(jwt);

			Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
			if (!usuarioOpt.isPresent()) {
				response.put("mensaje", "Usuario no encontrado");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Usuario usuario = usuarioOpt.get();
			UserInfoDTO userInfo = null;

			// PRIMERO VERIFICAR SI ES ADMIN (esto es lo más importante)
			boolean isAdmin = usuario.getRoles().stream()
					.anyMatch(rol -> rol.getRolNombre().name().equals("ROLE_ADMIN"));

			if (isAdmin) {
				// Si es ADMIN, priorizar este rol sobre cualquier otro
				userInfo = new UserInfoDTO(
						"Administrador", // o podrías tener un campo de nombre para admin
						"",
						"",
						"Administrador"
				);

				// Si el admin también tiene datos de empleado, los incluimos pero manteniendo el rol correcto
				Empleado empleado = empleadoService.findByUsuarioId(usuario.getId()).orElse(null);
				if (empleado != null) {
					userInfo.setNombres(empleado.getNombres());
					userInfo.setApellidoPaterno(empleado.getApellidoPaterno());
					userInfo.setApellidoMaterno(empleado.getApellidoMaterno());
					// Pero mantenemos el rol como "Administrador"
				}

			} else {
				// Si no es admin, entonces buscar como empleado o estudiante
				Empleado empleado = empleadoService.findByUsuarioId(usuario.getId()).orElse(null);
				if (empleado != null) {
					List<String> especialidadesNombres = empleado.getEspecialidades()
							.stream()
							.map(esp -> esp.getNombre())
							.collect(Collectors.toList());

					userInfo = new UserInfoDTO(
							empleado.getNombres(),
							empleado.getApellidoPaterno(),
							empleado.getApellidoMaterno(),
							"Profesor", // Aquí mantenemos Profesor para empleados
							empleado.getFechaNacimiento(),
							empleado.getCui(),
							empleado.getDomicilio(),
							empleado.getCelular(),
							empleado.getSexo(),
							empleado.getCorreo(),
							especialidadesNombres
					);
				} else {
					// Buscar información como estudiante
					Estudiante estudiante = estudianteService.findByUsuarioId(usuario.getId()).orElse(null);
					if (estudiante != null) {
						String gradoNombre = estudiante.getGrado() != null ? estudiante.getGrado().getNombre() : null;
						String nivelNombre = estudiante.getNivel() != null ? estudiante.getNivel().getNombre() : null;
						String turnoNombre = estudiante.getTurno() != null ? estudiante.getTurno().getNombre() : null;
						String aulaNombre = estudiante.getAulaEstudiante() != null ? estudiante.getAulaEstudiante().getNombre() : null;
						String apoderadoNombre = estudiante.getApoderado() != null ?
								estudiante.getApoderado().getNombres() + " " + estudiante.getApoderado().getApellidoPaterno() : null;
						String apoderadoCelular = estudiante.getApoderado() != null ? estudiante.getApoderado().getCelular() : null;

						userInfo = new UserInfoDTO(
								estudiante.getNombres(),
								estudiante.getApellidoPaterno(),
								estudiante.getApellidoMaterno(),
								"Estudiante",
								estudiante.getFechaNacimiento(),
								estudiante.getCui(),
								estudiante.getDomicilio(),
								estudiante.getCelular(),
								estudiante.getSexo(),
								estudiante.getCorreo(),
								gradoNombre,
								nivelNombre,
								turnoNombre,
								aulaNombre,
								apoderadoNombre,
								apoderadoCelular
						);
					}
				}
			}

			if (userInfo == null) {
				response.put("mensaje", "No se encontró información del usuario");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(userInfo, HttpStatus.OK);

		} catch (Exception e) {
			response.put("mensaje", "Error al obtener la información del usuario");
			response.put("error", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

/**
 * Endpoint para cambiar contraseña de usuario logueado
 */
@PutMapping("/cambiar-contrasena")
public ResponseEntity<?> cambiarContrasenaUsuarioLogueado(
        @Valid @RequestBody CambiarContrasenaDTO cambiarContrasenaDTO,
        BindingResult result,
        @RequestHeader("Authorization") String token) {
    
    Map<String, Object> response = new HashMap<>();
    
    // Validar errores de entrada
    if (result.hasErrors()) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                .collect(Collectors.toList());
        
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    try {
        // Extraer username del token
        String jwt = token.substring(7); // Remover "Bearer "
        String username = jwtProvider.getUsernameFromToken(jwt);
        
        // Intentar cambiar la contraseña
        boolean cambioExitoso = passwordService.cambiarContrasenaUsuarioLogueado(username, cambiarContrasenaDTO);
        
        if (cambioExitoso) {
            response.put("mensaje", "Contraseña actualizada correctamente");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("mensaje", "La contraseña actual es incorrecta");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
    } catch (Exception e) {
        response.put("mensaje", "Error al cambiar la contraseña");
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Endpoint para cambiar contraseña desde login usando CUI
 */
@PutMapping("/cambiar-contrasena-login")
public ResponseEntity<?> cambiarContrasenaDesdeLogin(
        @Valid @RequestBody CambiarContrasenaLoginDTO cambiarContrasenaLoginDTO,
        BindingResult result) {
    
    Map<String, Object> response = new HashMap<>();
    
    // Validar errores de entrada
    if (result.hasErrors()) {
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                .collect(Collectors.toList());
        
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    try {
        // Intentar cambiar la contraseña
        boolean cambioExitoso = passwordService.cambiarContrasenaDesdeLogin(cambiarContrasenaLoginDTO);
        
        if (cambioExitoso) {
            response.put("mensaje", "Contraseña actualizada correctamente");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("mensaje", "No se encontró un usuario con el CUI proporcionado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
    } catch (Exception e) {
        response.put("mensaje", "Error al cambiar la contraseña");
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Endpoint para validar si existe un usuario con el CUI dado
 */
@GetMapping("/validar-cui/{cui}")
public ResponseEntity<?> validarCui(@PathVariable String cui) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        Usuario usuario = passwordService.findUsuarioByCui(cui);
        
        if (usuario != null) {
            response.put("existe", true);
            response.put("mensaje", "Usuario encontrado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("existe", false);
            response.put("mensaje", "No se encontró un usuario con este CUI");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
    } catch (Exception e) {
        response.put("mensaje", "Error al validar el CUI");
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
	
	
}

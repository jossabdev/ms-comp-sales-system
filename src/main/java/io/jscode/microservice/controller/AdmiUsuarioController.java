package io.jscode.microservice.controller;

import java.net.URI;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.jscode.microservice.dto.AdmiUsuarioDTO;
import io.jscode.microservice.service.AdmiUsuarioService;
import io.jscode.microservice.service.impl.AdmiUsuarioServiceImpl;
import io.jscode.microservice.util.AdmiUsuarioValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;
import io.jscode.util.SalesUtils;

@RestController
@RequestMapping("usuarios")
public class AdmiUsuarioController {
    @Autowired
	private AdmiUsuarioService admiUsuarioService;
	
	@Autowired
	private BeanFactory factory;
	
	@Autowired
	AdmiUsuarioValidator admiUsuarioValidator;
	
	@Autowired
	SalesUtils salesUtils;
	
	private String admiUsuarioServiceImpl = "AdmiUsuarioServiceImpl";
	
	static final Logger log = LogManager.getLogger(AdmiUsuarioController.class);

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiUsuarioDTO> obtenerTodosLosUsuarios() {
		ResponseListGenerico<AdmiUsuarioDTO> response = new ResponseListGenerico<>();
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			response.setData(admiUsuarioService.obtenerTodosLosUsuarios());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - obtenerTodosLosUsuarios: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<AdmiUsuarioDTO> obtenerUsuarioPorId(@PathVariable(name = "idUsuario") Long idUsuario) {
		ResponseGenerico<AdmiUsuarioDTO> response = new ResponseGenerico<>();
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			response.setData(admiUsuarioService.obtenerUsuarioPorId(idUsuario));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - obtenerUsuarioPorId: {}", e.getMessage());
			e.printStackTrace();
			
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarUsuario", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarUsuario(@RequestHeader Map<String, String> headers, @RequestBody AdmiUsuarioDTO request)  {
		URI uri;
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			//validar guardar Usuario
			admiUsuarioValidator.validarGuardarUsuario(request, headers);
			
			AdmiUsuarioDTO usuarioGuardado = admiUsuarioService.guardarUsuario(request);
			uri = URI.create("/usuarios/" + usuarioGuardado.getIdUsuario());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - guardarUsuario: {}", e.getMessage());
			e.printStackTrace();
			if (e.getErrorCode().equals(422))
				throw new ResponseStatusException( HttpStatus.UNPROCESSABLE_ENTITY, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		
		return ResponseEntity				
				.created(uri)
				.body(new ResponseGenerico<>());
	}
	
	@PutMapping(path = "actualizarUsuario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarUsuario(@RequestHeader Map<String, String> headers, @RequestBody AdmiUsuarioDTO request)  {
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			AdmiUsuarioDTO usuarioDto = admiUsuarioValidator.validarActualizarUsuario(request, headers);
			admiUsuarioService.actualizarUsuario(usuarioDto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - actualizarUsuario: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarUsuario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarUsuario(@RequestHeader Map<String, String> headers, @RequestBody AdmiUsuarioDTO request)  {
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			AdmiUsuarioDTO usuarioDto = admiUsuarioValidator.validarEliminarUsuario(request, headers);
			admiUsuarioService.eliminarUsuario(usuarioDto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - eliminarUsuario: {}", e.getMessage());
			e.printStackTrace();
			if(e.getErrorCode().equals(404)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND , e.getErrorMessage(), e);
			}else {
				throw new ResponseStatusException(HttpStatus.CONFLICT , e.getErrorMessage(), e);
			}
		}
		return new ResponseGenerico<>();
	}
	
	@PostMapping(path = "consultarPor", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiUsuarioDTO> obtenerTodosLosUsuariosPor(@RequestBody AdmiUsuarioDTO request) {
		ResponseListGenerico<AdmiUsuarioDTO> response = new ResponseListGenerico<>();
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			response.setData(admiUsuarioService.obtenerTodosLosUsuariosPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - obtenerTodosLosUsuariosPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarUsuario/{idUsuario}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarUsuarioPorId(@PathVariable Long idUsuario)  {
		admiUsuarioService = (AdmiUsuarioServiceImpl) factory.getBean(admiUsuarioServiceImpl);
		try {
			admiUsuarioService.eliminarUsuarioPorId(idUsuario);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiUsuarioController - eliminarUsuarioPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}

}

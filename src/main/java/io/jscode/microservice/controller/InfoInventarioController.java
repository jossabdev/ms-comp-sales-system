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

import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.impl.InfoInventarioServiceImpl;
import io.jscode.microservice.util.InfoInventarioValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;

@RestController
@RequestMapping("inventarios")
public class InfoInventarioController {
	
	@Autowired
	private InfoInventarioService infoInventarioService;
	
	@Autowired
	InfoInventarioValidator infoInventarioValidator;
	
	@Autowired
	private BeanFactory factory;
	
	private String infoInventarioServiceImpl = "InfoInventarioServiceImpl";
	
	static final Logger log = LogManager.getLogger(InfoInventarioController.class);

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoInventarioDTO> obtenerTodosLosInventarios() {
		ResponseListGenerico<InfoInventarioDTO> response = new ResponseListGenerico<>();
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			response.setData(infoInventarioService.obtenerTodosLosInventarios());
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - obtenerTodosLosInventarios: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idInventario}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<InfoInventarioDTO> obtenerInventarioPorId(@PathVariable(name = "idInventario") Long idInventario) {
		ResponseGenerico<InfoInventarioDTO> response = new ResponseGenerico<>();
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			response.setData(infoInventarioService.obtenerInventarioPorId(idInventario));
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - obtenerInventarioPorId: {}", e.getMessage());
			e.printStackTrace();
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarInventario", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarInventario(@RequestHeader Map<String, String> headers, @RequestBody InfoInventarioDTO request)  {
		URI uri;
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			infoInventarioValidator.validarGuardarInventario(request, headers);
			InfoInventarioDTO inventarioGuardado = infoInventarioService.guardarInventario(request);
			uri = URI.create("/inventarios/" + inventarioGuardado.getIdInventario());
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - guardarInventario: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		
		return ResponseEntity				
				.created(uri)
				.body(new ResponseGenerico<>());
	}
	
	@PutMapping(path = "actualizarInventario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarInventario(@RequestHeader Map<String, String> headers, @RequestBody InfoInventarioDTO request)  {
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			InfoInventarioDTO inventarioDto = infoInventarioValidator.validarActualizarInventario(request, headers);
			infoInventarioService.actualizarInventario(inventarioDto);
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - actualizarInventario: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarInventario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarInventario(@RequestBody InfoInventarioDTO request)  {
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			infoInventarioService.eliminarInventario(request);
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - eliminarInventario: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@PostMapping(path = "consultarPor", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoInventarioDTO> obtenerTodosLosInventariosPor(@RequestBody InfoInventarioDTO request) {
		ResponseListGenerico<InfoInventarioDTO> response = new ResponseListGenerico<>();
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			response.setData(infoInventarioService.obtenerTodosLosInventariosPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - obtenerTodosLosInventariosPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarInventario/{idInventario}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarInventarioPorId(@PathVariable Long idInventario)  {
		infoInventarioService = (InfoInventarioServiceImpl) factory.getBean(infoInventarioServiceImpl);
		try {
			infoInventarioService.eliminarInventarioPorId(idInventario);
		} catch (ExcepcionGenerica e) {
			log.error("InfoInventarioController - eliminarInventario: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
}

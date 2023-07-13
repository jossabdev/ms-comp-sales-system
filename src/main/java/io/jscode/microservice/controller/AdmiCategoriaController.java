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

import io.jscode.microservice.dto.AdmiCategoriaDTO;
import io.jscode.microservice.service.AdmiCategoriaService;
import io.jscode.microservice.service.impl.AdmiCategoriaServiceImpl;
import io.jscode.microservice.util.AdmiCategoriaValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;
import io.jscode.util.SalesUtils;

@RestController
@RequestMapping("categorias")
public class AdmiCategoriaController {

	@Autowired
	private AdmiCategoriaService admiCategoriaService;
	
	@Autowired
	private AdmiCategoriaValidator admiCategoriaValidator;
	
	@Autowired
	private BeanFactory factory;
	
	@Autowired
	SalesUtils salesUtils;
	
	private String admiCategoriaServiceImpl = "AdmiCategoriaServiceImpl";
	
	static final Logger log = LogManager.getLogger(AdmiCategoriaController.class);
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiCategoriaDTO> obtenerTodasLasCategorias(@RequestHeader Map<String, String> headers) {
				
		ResponseListGenerico<AdmiCategoriaDTO> response = new ResponseListGenerico<>();

		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			response.setData(admiCategoriaService.obtenerTodasLasCategorias());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - obtenerTodasLasCategorias: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idCategoria}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<AdmiCategoriaDTO> obtenerCategoriaPorId(@PathVariable(name = "idCategoria") Long idCategoria, @RequestHeader Map<String, String> headers) {
		ResponseGenerico<AdmiCategoriaDTO> response = new ResponseGenerico<>();
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			response.setData(admiCategoriaService.obtenerCategoriaPorId(idCategoria));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - obtenerCategoriaPorId: {}", e.getMessage());
			e.printStackTrace();
			
			if (e.getErrorCode().equals(404))				
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarCategoria", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarCategoria(@RequestHeader Map<String, String> headers, @RequestBody AdmiCategoriaDTO request)  {
		URI uri;
		
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			//validar guardar categoria
			admiCategoriaValidator.validarGuardarCategoria(request, headers);
			AdmiCategoriaDTO categoriaGuardada = admiCategoriaService.guardarCategoria(request);
			uri = URI.create("/categorias/" + categoriaGuardada.getIdCategoria());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - guardarCategoria: {}", e.getMessage());
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
	
	@PutMapping(path = "actualizarCategoria", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarCategoria(@RequestHeader Map<String, String> headers, @RequestBody AdmiCategoriaDTO request)  {
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			AdmiCategoriaDTO categoriaDto = admiCategoriaValidator.validarActualizarCategoria(request, headers);
			admiCategoriaService.actualizarCategoria(categoriaDto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - actualizarCategoria: {}", e.getMessage());
			e.printStackTrace();
			if (e.getErrorCode().equals(422))				
				throw new ResponseStatusException( HttpStatus.UNPROCESSABLE_ENTITY, e.getErrorMessage(), e);
			else if (e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarCategoria", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarCategoria(@RequestHeader Map<String, String> headers, @RequestBody AdmiCategoriaDTO request)  {
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			AdmiCategoriaDTO categoria = admiCategoriaValidator.validarEliminarCategoria(request, headers);
			admiCategoriaService.eliminarCategoria(categoria);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - eliminarCategoria: {}", e.getMessage());
			e.printStackTrace();
			if(e.getErrorCode().equals(404)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND , e.getErrorMessage(), e);
			}else {
				throw new ResponseStatusException(HttpStatus.CONFLICT , e.getErrorMessage(), e);
			}
		}
		return new ResponseGenerico<>();
	}
	
	@PostMapping(path = "consultarPor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiCategoriaDTO> obtenerTodasLasCategoriasPor(@RequestHeader Map<String, String> headers, @RequestBody AdmiCategoriaDTO request) {
		ResponseListGenerico<AdmiCategoriaDTO> response = new ResponseListGenerico<>();
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			response.setData(admiCategoriaService.obtenerTodasLasCategoriasPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - obtenerTodasLasCategoriasPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND , e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarCategoria/{idCategoria}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarCategoriaPorId(@RequestHeader Map<String, String> headers, @PathVariable Long idCategoria)  {
		admiCategoriaService = (AdmiCategoriaServiceImpl) factory.getBean(admiCategoriaServiceImpl);
		try {
			admiCategoriaService.eliminarCategoriaPorId(idCategoria);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiCategoriaController - eliminarCategoriaPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
}

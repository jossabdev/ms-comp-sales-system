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

import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.microservice.service.AdmiProductoService;
import io.jscode.microservice.service.impl.AdmiProductoServiceImpl;
import io.jscode.microservice.util.AdmiProductoValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;
import io.jscode.util.SalesUtils;

@RestController
@RequestMapping("productos")
public class AdmiProductoController {
	@Autowired
	private AdmiProductoService admiProductoService;
	
	@Autowired
	private BeanFactory factory;
	
	@Autowired
	AdmiProductoValidator admiProductoValidator;
	
	@Autowired
	SalesUtils salesUtils;
	
	private String admiProductoServiceImpl = "AdmiProductoServiceImpl";
	
	static final Logger log = LogManager.getLogger(AdmiProductoController.class);

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiProductoDTO> obtenerTodosLosProductos() {
		ResponseListGenerico<AdmiProductoDTO> response = new ResponseListGenerico<>();
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			response.setData(admiProductoService.obtenerTodosLosProductos());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - obtenerTodosLosProductos: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idProducto}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<AdmiProductoDTO> obtenerProductoPorId(@PathVariable(name = "idProducto") Long idProducto) {
		ResponseGenerico<AdmiProductoDTO> response = new ResponseGenerico<>();
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			response.setData(admiProductoService.obtenerProductoPorId(idProducto));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - obtenerProductoPorId: {}", e.getMessage());
			e.printStackTrace();
			
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarProducto", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarProducto(@RequestHeader Map<String, String> headers, @RequestBody AdmiProductoDTO request)  {
		URI uri;
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			//validar guardar producto
			admiProductoValidator.validarGuardarProducto(request, headers);
			
			AdmiProductoDTO productoGuardado = admiProductoService.guardarProducto(request);
			uri = URI.create("/productos/" + productoGuardado.getIdProducto());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - guardarProducto: {}", e.getMessage());
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
	
	@PutMapping(path = "actualizarProducto", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarProducto(@RequestHeader Map<String, String> headers, @RequestBody AdmiProductoDTO request)  {
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			AdmiProductoDTO productoDto = admiProductoValidator.validarActualizarProducto(request, headers);
			admiProductoService.actualizarProducto(productoDto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - actualizarProducto: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarProducto", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarProducto(@RequestHeader Map<String, String> headers, @RequestBody AdmiProductoDTO request)  {
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			AdmiProductoDTO productoDto = admiProductoValidator.validarEliminarProducto(request, headers);
			admiProductoService.eliminarProducto(productoDto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - eliminarProducto: {}", e.getMessage());
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
	public ResponseListGenerico<AdmiProductoDTO> obtenerTodosLosProductosPor(@RequestBody AdmiProductoDTO request) {
		ResponseListGenerico<AdmiProductoDTO> response = new ResponseListGenerico<>();
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			response.setData(admiProductoService.obtenerTodosLosProductosPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - obtenerTodosLosProductosPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarProducto/{idProducto}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarProductoPorId(@PathVariable Long idProducto)  {
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			admiProductoService.eliminarProductoPorId(idProducto);
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - eliminarProductoPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}

	@GetMapping(path = "obtenerTodosLosProductosEnStock", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiProductoDTO> obtenerTodosLosProductosEnStock() {
		ResponseListGenerico<AdmiProductoDTO> response = new ResponseListGenerico<>();
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			response.setData(admiProductoService.obtenerTodosLosProductosEnStock());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiProductoController - obtenerTodosLosProductosEnStock: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}

	@PostMapping(path = "obtenerProductoEnStockPorCodigoBarras", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<AdmiProductoDTO> obtenerProductoEnStockPorCodigoBarras(@RequestBody AdmiProductoDTO request) {
		ResponseGenerico<AdmiProductoDTO> response = new ResponseGenerico<>();
		admiProductoService = (AdmiProductoServiceImpl) factory.getBean(admiProductoServiceImpl);
		try {
			response.setData(admiProductoService.obtenerProductoEnStockPorCodigoBarras(request));
		} catch (ExcepcionGenerica e) {
			if(e.getErrorCode().equals(404)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND , e.getErrorMessage(), e);
			}else {
				throw new ResponseStatusException(HttpStatus.CONFLICT , e.getErrorMessage(), e);
			}
		}
		return response;
	}
}

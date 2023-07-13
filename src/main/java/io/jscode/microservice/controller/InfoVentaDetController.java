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

import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.microservice.service.InfoVentaDetService;
import io.jscode.microservice.service.impl.InfoVentaDetServiceImpl;
import io.jscode.microservice.util.InfoVentaDetValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;

@RestController
@RequestMapping("detalleVentas")
public class InfoVentaDetController {
	@Autowired
	private InfoVentaDetService infoVentaDetService;
	
	@Autowired
	InfoVentaDetValidator infoVentaDetValidator;
	
	@Autowired
	private BeanFactory factory;
	
	private String infoVentaDetServiceImpl = "InfoVentaDetServiceImpl";
	
	static final Logger log = LogManager.getLogger(InfoVentaDetController.class);

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentas() {
		ResponseListGenerico<InfoVentaDetDTO> response = new ResponseListGenerico<>();
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			response.setData(infoVentaDetService.obtenerTodosLosDetallesDeLasVentas());
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - obtenerTodosLosDetallesDeLasVentas: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idDetalleVenta}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<InfoVentaDetDTO> obtenerDetalleVentaPorId(@PathVariable(name = "idDetalleVenta") Long idDetalleVenta) {
		ResponseGenerico<InfoVentaDetDTO> response = new ResponseGenerico<>();
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			response.setData(infoVentaDetService.obtenerDetalleVentaPorId(idDetalleVenta));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - obtenerDetalleVentaPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarDetalleVenta", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarDetalleVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaDetDTO request)  {
		URI uri;
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			infoVentaDetValidator.validarGuardarDetalleVenta(request, headers);
			InfoVentaDetDTO detalleVentaGuardada = infoVentaDetService.guardarDetalleVenta(request);
			uri = URI.create("/detalleVentas/" + detalleVentaGuardada.getIdVentaDet());
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - guardarDetalleVenta: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		
		return ResponseEntity				
				.created(uri)
				.body(new ResponseGenerico<>());
	}
	
	@PutMapping(path = "actualizarDetalleVenta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarDetalleVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaDetDTO request)  {
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			InfoVentaDetDTO detalleVentaDto = infoVentaDetValidator.validarActualizarInfoVentaDet(request, headers);
			infoVentaDetService.actualizarDetalleVenta(detalleVentaDto);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - actualizarDetalleVenta: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarDetalleVenta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarDetalleVenta(@RequestBody InfoVentaDetDTO request)  {
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			
			infoVentaDetService.eliminarDetalleVenta(request);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - eliminarDetalleVenta: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@PostMapping(path = "consultarPor", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentasPor(@RequestBody InfoVentaDetDTO request) {
		ResponseListGenerico<InfoVentaDetDTO> response = new ResponseListGenerico<>();
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			response.setData(infoVentaDetService.obtenerTodosLosDetallesDeLasVentasPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - obtenerTodosLosDetallesDeLasVentasPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarDetalleVenta/{idDetalleVenta}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarDetalleVentaPorId(@PathVariable Long idDetalleVenta)  {
		infoVentaDetService = (InfoVentaDetServiceImpl) factory.getBean(infoVentaDetServiceImpl);
		try {
			infoVentaDetService.eliminarDetalleVentaPorId(idDetalleVenta);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaDetController - eliminarDetalleVentaPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
}

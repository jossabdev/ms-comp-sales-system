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

import io.jscode.microservice.dto.ConsultaVentasDTO;
import io.jscode.microservice.dto.ConsultaVentasReqDTO;
import io.jscode.microservice.dto.EstadisticaVentaDTO;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.service.GestionVentaService;
import io.jscode.microservice.service.InfoVentaCabService;
import io.jscode.microservice.service.impl.GestionVentaServiceImpl;
import io.jscode.microservice.service.impl.InfoVentaCabServiceImpl;
import io.jscode.microservice.util.InfoVentaCabValidator;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseGenerico;
import io.jscode.util.ResponseListGenerico;

@RestController
@RequestMapping("ventas")
public class InfoVentaCabController {
	
	@Autowired
	InfoVentaCabService infoVentaService;
	
	@Autowired
	InfoVentaCabValidator infoVentaCabValidator;
	
	@Autowired
	GestionVentaService gestionVentaService;
	
	@Autowired
	private BeanFactory factory;
	
	private String infoVentaCabServiceImpl = "InfoVentaCabServiceImpl";
	private String gestionVentaServiceImpl = "GestionVentaServiceImpl";
	
	static final Logger log = LogManager.getLogger(InfoVentaCabController.class);
	
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoVentaCabDTO> obtenerTodasLasVentas() {
		ResponseListGenerico<InfoVentaCabDTO> response = new ResponseListGenerico<>();
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			response.setData(infoVentaService.obtenerTodasLasVentas());
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTodasLasVentas: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@GetMapping(path = "/{idVenta}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<InfoVentaCabDTO> obtenerVentaPorId(@PathVariable(name = "idVenta") Long idVenta) {
		ResponseGenerico<InfoVentaCabDTO> response = new ResponseGenerico<>();
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			response.setData(infoVentaService.obtenerVentaPorId(idVenta));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerVentaPorId: {}", e.getMessage());
			e.printStackTrace();
			
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@PostMapping(path = "guardarVenta", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)	
	public ResponseEntity<ResponseGenerico<?>> guardarVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaCabDTO request)  {
		URI uri;
		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);
		try {
			infoVentaCabValidator.validarGuardarVenta(request, headers);
			Long idVenta = gestionVentaService.proceRegistrarVenta(request);
			uri = URI.create("/ventas/" + idVenta);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - guardarVenta: {}", e.getMessage());
			e.printStackTrace();
			
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		
		return ResponseEntity				
				.created(uri)
				.body(new ResponseGenerico<>());
	}
	
	@PutMapping(path = "actualizarVenta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> actualizarVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaCabDTO request)  {
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			InfoVentaCabDTO ventaDto = infoVentaCabValidator.validarActualizarInfoVenta(request, headers);
			infoVentaService.actualizarVenta(ventaDto);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - actualizarVenta: {}", e.getMessage());
			e.printStackTrace();
			
			if(e.getErrorCode().equals(404))
				throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
			else
				throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}
	
	@DeleteMapping(path = "eliminarVenta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaCabDTO request)  {
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			InfoVentaCabDTO venta = infoVentaCabValidator.validarEliminarVentaCab(request, headers);
			infoVentaService.eliminarVenta(venta);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - eliminarVenta: {}", e.getMessage());
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
	public ResponseListGenerico<InfoVentaCabDTO> obtenerTodasLasVentasPor(@RequestBody InfoVentaCabDTO request) {
		ResponseListGenerico<InfoVentaCabDTO> response = new ResponseListGenerico<>();
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			response.setData(infoVentaService.obtenerTodasLasVentasPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTodasLasVentasPor: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}

	@PostMapping(path = "consultarPorRangoFecha", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<InfoVentaCabDTO> obtenerTodasLasVentasPorRangoFecha(@RequestBody ConsultaVentasReqDTO request) {
		ResponseListGenerico<InfoVentaCabDTO> response = new ResponseListGenerico<>();
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			response.setData(gestionVentaService.obtenerVentasPorRangoFecha(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTodasLasVentasPorRangoFecha: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
	
	@DeleteMapping(path = "eliminarVenta/{idVenta}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseGenerico<?> eliminarVentaPorId(@PathVariable Long idVenta)  {
		infoVentaService = (InfoVentaCabServiceImpl) factory.getBean(infoVentaCabServiceImpl);
		try {
			infoVentaService.eliminarVentaPorId(idVenta);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - eliminarVentaPorId: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}

	@PostMapping(path = "obtenerTotalDeVentas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<ConsultaVentasDTO> obtenerTotalDeVentasPorRangoDeFecha(@RequestBody ConsultaVentasReqDTO request){
		ResponseGenerico<ConsultaVentasDTO> response = new ResponseGenerico<>();
		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);
		try {
			response.setData(gestionVentaService.obtenerTotalDeVentasPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTotalDeVentasPorRango: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return response;
	}

	@PostMapping(path = "obtenerProductoTop", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<ConsultaVentasDTO> obtenerProductoTopPorRangoDeFecha(@RequestBody ConsultaVentasReqDTO request){
		ResponseGenerico<ConsultaVentasDTO> response = new ResponseGenerico<>();
		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);
		try {
			response.setData(gestionVentaService.obtenerProductoTop(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerProductoTopPorRangoDeFecha: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return response;
	}

	@PostMapping(path = "obtenerTotalDeGanancias", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<ConsultaVentasDTO> obtenerTotalDeGananciasPorRangoDeFecha(@RequestBody ConsultaVentasReqDTO request){
		ResponseGenerico<ConsultaVentasDTO> response = new ResponseGenerico<>();
		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);
		try {
			response.setData(gestionVentaService.obtenerTotalGananciasPor(request));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTotalDeVentasPorRango: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return response;
	}

	// obtenerTotalVentasPorCriterios: 6 meses, 30 dias, 7 dias
	@GetMapping(path = "obtenerEstadisticasVentasPorOpcion/{opcion}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<EstadisticaVentaDTO> obtenerEstadisticasVentas(@PathVariable Integer opcion){
		ResponseGenerico<EstadisticaVentaDTO> response = new ResponseGenerico<>();

		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);

		try {
			response.setData(gestionVentaService.obtenerEstadisticasVentasPorOpcion(opcion));
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerEstadisticasVentas: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return response;
	}

	@PostMapping(path = "anularVenta", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<?> anularVenta(@RequestHeader Map<String, String> headers, @RequestBody InfoVentaCabDTO request){
		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(GestionVentaServiceImpl.class);
		try {
			infoVentaCabValidator.validarAnularVenta(request, headers);
			gestionVentaService.proceAnularVenta(request);
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - anularVenta: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return new ResponseGenerico<>();
	}

	@GetMapping(path = "obtenerTotalDeInversion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseGenerico<ConsultaVentasDTO> obtenerTotalDeInversion(){
		ResponseGenerico<ConsultaVentasDTO> response = new ResponseGenerico<>();

		gestionVentaService = (GestionVentaServiceImpl) factory.getBean(gestionVentaServiceImpl);

		try {
			response.setData(gestionVentaService.obtenerTotalInversion());
		} catch (ExcepcionGenerica e) {
			log.error("InfoVentaCabController - obtenerTotalDeInversion: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, e.getErrorMessage(), e);
		}
		return response;
	}
}

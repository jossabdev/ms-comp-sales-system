package io.jscode.microservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.jscode.microservice.dto.AdmiRolDTO;
import io.jscode.microservice.service.AdmiRolService;
import io.jscode.microservice.service.impl.AdmiRolServiceImpl;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.ResponseListGenerico;
import io.jscode.util.SalesUtils;

@RestController
@RequestMapping("roles")
public class AdmiRolController {
    
    @Autowired
	private AdmiRolService admiRolService;
	
	@Autowired
	private BeanFactory factory;
	
	@Autowired
	SalesUtils salesUtils;
	
	private String admiRolServiceImpl = "AdmiRolServiceImpl";
	
	static final Logger log = LogManager.getLogger(AdmiRolController.class);

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseListGenerico<AdmiRolDTO> obtenerTodosLosRoles() {
		ResponseListGenerico<AdmiRolDTO> response = new ResponseListGenerico<>();
		admiRolService = (AdmiRolServiceImpl) factory.getBean(admiRolServiceImpl);
		try {
			response.setData(admiRolService.obtenerTodosLosRoles());
		} catch (ExcepcionGenerica e) {
			log.error("AdmiRolController - obtenerTodosLosRoles: {}", e.getMessage());
			e.printStackTrace();
			throw new ResponseStatusException( HttpStatus.CONFLICT, e.getErrorMessage(), e);
		}
		return response;
	}
}

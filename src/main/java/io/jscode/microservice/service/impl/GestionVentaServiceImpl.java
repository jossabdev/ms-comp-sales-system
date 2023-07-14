package io.jscode.microservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.InfoVentaCab;
import io.jscode.db.service.DBGestionVentasService;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.service.GestionVentaService;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("GestionVentaServiceImpl")
public class GestionVentaServiceImpl implements GestionVentaService{

	@Autowired
	DBGestionVentasService gestionVentasservice;
	
	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public Long proceRegistrarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {	
		InfoVentaCab ventaCab = salesUtils.mapper(request, InfoVentaCab.class);
		return gestionVentasservice.proceRegistrarVenta(ventaCab);
	}

}

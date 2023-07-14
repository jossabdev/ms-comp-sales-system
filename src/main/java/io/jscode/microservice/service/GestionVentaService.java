package io.jscode.microservice.service;

import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.util.ExcepcionGenerica;

public interface GestionVentaService {
	public Long proceRegistrarVenta(InfoVentaCabDTO ventaCab) throws ExcepcionGenerica;
}

package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.util.ExcepcionGenerica;

public interface InfoVentaCabService {
	List<InfoVentaCabDTO> obtenerTodasLasVentas() throws ExcepcionGenerica;
	InfoVentaCabDTO obtenerVentaPorId(Long id) throws ExcepcionGenerica;
	InfoVentaCabDTO guardarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica;
	void actualizarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica;
	void eliminarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica;
	void eliminarVentaPorId(Long id) throws ExcepcionGenerica;
	List<InfoVentaCabDTO> obtenerTodasLasVentasPor(InfoVentaCabDTO request) throws ExcepcionGenerica;
}

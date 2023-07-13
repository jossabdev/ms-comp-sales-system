package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.util.ExcepcionGenerica;

public interface InfoVentaDetService {
	List<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentas() throws ExcepcionGenerica;
	InfoVentaDetDTO obtenerDetalleVentaPorId(Long id) throws ExcepcionGenerica;
	InfoVentaDetDTO guardarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica;
	void actualizarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica;
	void eliminarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica;
	void eliminarDetalleVentaPorId(Long id) throws ExcepcionGenerica;
	List<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentasPor(InfoVentaDetDTO request) throws ExcepcionGenerica;
}

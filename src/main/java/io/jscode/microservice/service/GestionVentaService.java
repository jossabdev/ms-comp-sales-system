package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.ConsultaVentasDTO;
import io.jscode.microservice.dto.ConsultaVentasReqDTO;
import io.jscode.microservice.dto.EstadisticaVentaDTO;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.util.ExcepcionGenerica;

public interface GestionVentaService {
	public Long proceRegistrarVenta(InfoVentaCabDTO ventaCab) throws ExcepcionGenerica;
	public List<InfoVentaCabDTO> obtenerVentasPorRangoFecha(ConsultaVentasReqDTO request) throws ExcepcionGenerica;
	public ConsultaVentasDTO obtenerTotalDeVentasPor(ConsultaVentasReqDTO request) throws ExcepcionGenerica;
	public ConsultaVentasDTO obtenerTotalGananciasPor(ConsultaVentasReqDTO request) throws ExcepcionGenerica;
	public ConsultaVentasDTO obtenerProductoTop(ConsultaVentasReqDTO request) throws ExcepcionGenerica;
	public EstadisticaVentaDTO obtenerEstadisticasVentasPorOpcion(Integer opcion) throws ExcepcionGenerica;
	public void proceAnularVenta(InfoVentaCabDTO ventaCab) throws ExcepcionGenerica;
	public ConsultaVentasDTO obtenerTotalInversion() throws ExcepcionGenerica;
}

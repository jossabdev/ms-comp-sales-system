package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.util.ExcepcionGenerica;

public interface AdmiProductoService {
	List<AdmiProductoDTO> obtenerTodosLosProductos() throws ExcepcionGenerica;
	AdmiProductoDTO obtenerProductoPorId(Long id) throws ExcepcionGenerica;
	AdmiProductoDTO obtenerProductoPor(AdmiProductoDTO request) throws ExcepcionGenerica;
	AdmiProductoDTO guardarProducto(AdmiProductoDTO request) throws ExcepcionGenerica;
	void actualizarProducto(AdmiProductoDTO request) throws ExcepcionGenerica;
	void eliminarProducto(AdmiProductoDTO request) throws ExcepcionGenerica;
	void eliminarProductoPorId(Long id) throws ExcepcionGenerica;
	List<AdmiProductoDTO> obtenerTodosLosProductosPor(AdmiProductoDTO request) throws ExcepcionGenerica;
	List<AdmiProductoDTO> obtenerTodosLosProductosEnStock() throws ExcepcionGenerica;
	AdmiProductoDTO obtenerProductoEnStockPorCodigoBarras(AdmiProductoDTO request) throws ExcepcionGenerica;
}

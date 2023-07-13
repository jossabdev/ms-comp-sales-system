package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.util.ExcepcionGenerica;

public interface InfoInventarioService {
	List<InfoInventarioDTO> obtenerTodosLosInventarios() throws ExcepcionGenerica;
	InfoInventarioDTO obtenerInventarioPorId(Long id) throws ExcepcionGenerica;
	InfoInventarioDTO obtenerInventarioPor(InfoInventarioDTO inventario) throws ExcepcionGenerica;
	InfoInventarioDTO guardarInventario(InfoInventarioDTO request) throws ExcepcionGenerica;
	void actualizarInventario(InfoInventarioDTO request) throws ExcepcionGenerica;
	void eliminarInventario(InfoInventarioDTO request) throws ExcepcionGenerica;
	void eliminarInventarioPorId(Long id) throws ExcepcionGenerica;
	List<InfoInventarioDTO> obtenerTodosLosInventariosPor(InfoInventarioDTO request) throws ExcepcionGenerica;
}

package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.AdmiCategoriaDTO;
import io.jscode.util.ExcepcionGenerica;

public interface AdmiCategoriaService {
	
	List<AdmiCategoriaDTO> obtenerTodasLasCategorias() throws ExcepcionGenerica;
	AdmiCategoriaDTO obtenerCategoriaPorId(Long id) throws ExcepcionGenerica;
	AdmiCategoriaDTO obtenerCategoriaPor(AdmiCategoriaDTO request) throws ExcepcionGenerica;
	AdmiCategoriaDTO guardarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica;
	void actualizarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica;
	void eliminarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica;
	void eliminarCategoriaPorId(Long id) throws ExcepcionGenerica;
	List<AdmiCategoriaDTO> obtenerTodasLasCategoriasPor(AdmiCategoriaDTO request) throws ExcepcionGenerica;
	
}

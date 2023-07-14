package io.jscode.microservice.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.InfoInventario;
import io.jscode.db.service.DBInfoInventarioService;
import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("InfoInventarioServiceImpl")
public class InfoInventarioServiceImpl implements InfoInventarioService {

	@Autowired
	DBInfoInventarioService infoInventarioService;
	
	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public List<InfoInventarioDTO> obtenerTodosLosInventarios() throws ExcepcionGenerica {
		List<InfoInventario> inventarios = infoInventarioService.getAll();
		return salesUtils.mapperList(inventarios, InfoInventarioDTO.class);
	}

	@Override
	public InfoInventarioDTO obtenerInventarioPorId(Long id) throws ExcepcionGenerica {
		InfoInventario inventario; 
		try {
			inventario = infoInventarioService.getById(id);
		}catch(NoSuchElementException e) {
			throw new ExcepcionGenerica("El inventario con id "+ id + " no existe. Detalle de error: "+ e.getMessage(), 404);
		}
		return salesUtils.mapper(inventario, InfoInventarioDTO.class);
	}

	@Override
	public InfoInventarioDTO guardarInventario(InfoInventarioDTO request) throws ExcepcionGenerica {
		InfoInventario inventario = salesUtils.mapper(request, InfoInventario.class);
		InfoInventario inventarioGuardado;
		try {
			inventarioGuardado = infoInventarioService.save(inventario);
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar el inventario: " + e.getMessage());
		}
		return salesUtils.mapper(inventarioGuardado, InfoInventarioDTO.class);
	}

	@Override
	public void actualizarInventario(InfoInventarioDTO request) throws ExcepcionGenerica {		
		InfoInventario inventario = salesUtils.mapper(request, InfoInventario.class);
		infoInventarioService.update(inventario);
	}

	@Override
	public void eliminarInventario(InfoInventarioDTO request) throws ExcepcionGenerica {
		// Se realiza eliminado lógico
		request.setEstado(Constantes.ESTADO_INACTIVO);		
		actualizarInventario(request);

	}

	@Override
	public List<InfoInventarioDTO> obtenerTodosLosInventariosPor(InfoInventarioDTO request) throws ExcepcionGenerica {
		InfoInventario inventario = salesUtils.mapper(request, InfoInventario.class);
		List<InfoInventario> inventariosFiltrados = infoInventarioService.getAllBy(inventario);
		return salesUtils.mapperList(inventariosFiltrados, InfoInventarioDTO.class);
	}

	@Override
	public void eliminarInventarioPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta producto 
			InfoInventario inventarioExistente = infoInventarioService.getById(id);
			
			if(inventarioExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("El inventario ingresado no existe. Detalle error: "+e.getMessage(), 404);
		}
		
		infoInventarioService.deleteById(id);
	}

	@Override
	public InfoInventarioDTO obtenerInventarioPor(InfoInventarioDTO request) throws ExcepcionGenerica {
		InfoInventario inventario = salesUtils.mapper(request, InfoInventario.class);
		InfoInventario inventarioEncontrado = infoInventarioService.getBy(inventario);
		return salesUtils.mapper(inventarioEncontrado, InfoInventarioDTO.class);
	}
	
}

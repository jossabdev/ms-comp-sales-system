package io.jscode.microservice.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiProducto;
import io.jscode.db.service.DBAdmiProductoService;
import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.microservice.service.AdmiProductoService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("AdmiProductoServiceImpl")
public class AdmiProductoServiceImpl implements AdmiProductoService {

	@Autowired
	DBAdmiProductoService admiProductoService;

	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public List<AdmiProductoDTO> obtenerTodosLosProductos() throws ExcepcionGenerica {
		List<AdmiProducto> productos = admiProductoService.getAll();
		return salesUtils.mapperList(productos, AdmiProductoDTO.class);
	}

	@Override
	public AdmiProductoDTO obtenerProductoPorId(Long id) throws ExcepcionGenerica {
		AdmiProducto producto = null;
	
		try {
			producto = admiProductoService.getById(id);
		}
		catch(NoSuchElementException e) {
			throw new ExcepcionGenerica("El producto con id "+ id + " no existe. Detalle de error: "+ e.getMessage(), 404);
		}
		return salesUtils.mapper(producto, AdmiProductoDTO.class);
	}

	@Override
	public AdmiProductoDTO guardarProducto(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProducto producto = salesUtils.mapper(request, AdmiProducto.class);
		AdmiProducto productoGuardado;
		try {
			productoGuardado = admiProductoService.save(producto);
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar el producto: " + e.getMessage());
		}
		return salesUtils.mapper(productoGuardado, AdmiProductoDTO.class);
	}

	@Override
	public void actualizarProducto(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProducto producto = salesUtils.mapper(request, AdmiProducto.class);
		admiProductoService.update(producto);		
	}

	@Override
	public void eliminarProducto(AdmiProductoDTO request) throws ExcepcionGenerica {
		// se aplica borrado logico
		request.setEstado(Constantes.ESTADO_INACTIVO);
        actualizarProducto(request);		
	}

	@Override
	public List<AdmiProductoDTO> obtenerTodosLosProductosPor(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProducto producto = salesUtils.mapper(request, AdmiProducto.class);
		List<AdmiProducto> productosFitrados = admiProductoService.getAllBy(producto);
		return salesUtils.mapperList(productosFitrados, AdmiProductoDTO.class);
	}

	@Override
	public void eliminarProductoPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta producto 
			AdmiProducto productoExistente = admiProductoService.getById(id);
			
			if(productoExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("");
			}
		
		}catch(Exception e) {
			throw new ExcepcionGenerica("El producto ingresado no existe. Detalle error: "+e.getMessage(), 404);
		}
		admiProductoService.deleteById(id);
	}

	@Override
	public AdmiProductoDTO obtenerProductoPor(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProducto producto = salesUtils.mapper(request, AdmiProducto.class);
		AdmiProducto productoEncontrado = admiProductoService.getBy(producto);
		return salesUtils.mapper(productoEncontrado, AdmiProductoDTO.class);
	}

}

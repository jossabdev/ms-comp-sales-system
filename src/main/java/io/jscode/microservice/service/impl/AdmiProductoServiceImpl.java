package io.jscode.microservice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiProducto;
import io.jscode.db.entity.InfoInventario;
import io.jscode.db.service.DBAdmiProductoService;
import io.jscode.db.service.DBInfoInventarioService;
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
	DBInfoInventarioService infoInventarioService;

	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public List<AdmiProductoDTO> obtenerTodosLosProductos() throws ExcepcionGenerica {
		List<AdmiProducto> productos = admiProductoService.getAll()
		                .stream()
		                .filter(productoTmp -> !productoTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiProducto::getIdProducto).thenComparing(AdmiProducto::getEstado))
						.collect(Collectors.toList());
						
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
		List<AdmiProducto> productosFitrados = admiProductoService.getAllBy(producto)
		                .stream()
		                .filter(productoTmp -> !productoTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiProducto::getIdProducto).thenComparing(AdmiProducto::getEstado))
						.collect(Collectors.toList());

		return salesUtils.mapperList(productosFitrados, AdmiProductoDTO.class);
	}

	@Override
	public void eliminarProductoPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta producto 
			AdmiProducto productoExistente = admiProductoService.getById(id);
			
			if(productoExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("El producto ingresado no existe. Detalle error: "+e.getMessage(), 404);
		}
		admiProductoService.deleteById(id);
	}

	@Override
	public AdmiProductoDTO obtenerProductoPor(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProducto producto = salesUtils.mapper(request, AdmiProducto.class);
		AdmiProducto productoEncontrado = null;
		try{
			productoEncontrado = admiProductoService.getBy(producto);
		}catch(NoSuchElementException e){
			throw new ExcepcionGenerica(e.getMessage(), 404);
		}catch(Exception e){
			throw new ExcepcionGenerica(e.getMessage());
		}
		
		return salesUtils.mapper(productoEncontrado, AdmiProductoDTO.class);
	}

	@Override
	public List<AdmiProductoDTO> obtenerTodosLosProductosEnStock() throws ExcepcionGenerica {
		List<AdmiProducto> productos = admiProductoService.getAll()
		                .stream()
		                .filter(productoTmp -> !productoTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiProducto::getIdProducto).thenComparing(AdmiProducto::getEstado))
						.collect(Collectors.toList());

		productos.removeIf( producto -> {
			boolean seElimina = false;
			InfoInventario inventarioProducto = new InfoInventario();
			inventarioProducto.setProducto(producto);
			//inventarioProducto.setEstado(Constantes.ESTADO_ACTIVO);
			InfoInventario inventario;
			try{
				inventario = infoInventarioService.getBy(inventarioProducto);	
				if(inventario.getStockTotal() == 0){
					seElimina = true;
				}
			}catch(Exception e){
				seElimina = true;
			}/*catch(Exception e){
				seElimina = true;
			}*/
			return seElimina;
		});

		return salesUtils.mapperList(productos, AdmiProductoDTO.class);
	}

	@Override
	public AdmiProductoDTO obtenerProductoEnStockPorCodigoBarras(AdmiProductoDTO request) throws ExcepcionGenerica {
		AdmiProductoDTO productoDto = null;
		try{
			productoDto = this.obtenerProductoPor(request);
		}catch(ExcepcionGenerica ex){
			throw new ExcepcionGenerica("No existe producto con codigo de barras: "+ request.getCodigoBarras(), 404);
		}
		 
		AdmiProducto producto = salesUtils.mapper(productoDto, AdmiProducto.class);

		InfoInventario inventarioProducto = new InfoInventario();
		inventarioProducto.setProducto(producto);
		InfoInventario inventario;
		try{
			inventario = infoInventarioService.getBy(inventarioProducto);	
			if(inventario.getStockTotal() == 0){
				throw new ExcepcionGenerica("No hay stock para el producto: "+ producto.getNombreProducto(), 404);
			}
		}catch(Exception e){
			throw new ExcepcionGenerica("No existe inventario para el producto "+ producto.getNombreProducto(), 404);
		}
		return productoDto;
	}

}

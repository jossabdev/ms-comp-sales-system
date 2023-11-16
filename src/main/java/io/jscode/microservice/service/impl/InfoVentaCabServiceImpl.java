package io.jscode.microservice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import io.jscode.db.entity.InfoVentaCab;
import io.jscode.db.service.DBInfoVentaCabService;
import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.InfoVentaCabService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("InfoVentaCabServiceImpl")
public class InfoVentaCabServiceImpl implements InfoVentaCabService {
    
	@Autowired
	DBInfoVentaCabService infoVentaService;
	
	@Autowired
	InfoInventarioService infoInventarioService;
	
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	private String infoInventarioServiceImpl = "InfoInventarioServiceImpl";
	
	@Override
	public List<InfoVentaCabDTO> obtenerTodasLasVentas() throws ExcepcionGenerica {
		List<InfoVentaCab> ventas = infoVentaService.getAll()
		                .stream()
		                .filter(ventasTmp -> !ventasTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(InfoVentaCab::getFechaVenta).reversed())
						.collect(Collectors.toList());
						
		return salesUtils.mapperList(ventas, InfoVentaCabDTO.class);
	}

	@Override
	public InfoVentaCabDTO obtenerVentaPorId(Long id) throws ExcepcionGenerica {
		InfoVentaCab venta;
		try {
			venta = infoVentaService.getById(id);	
		}catch(NoSuchElementException e) {
			throw new ExcepcionGenerica("La venta con id "+ id + " no existe. Detalle de error: "+ e.getMessage(), 404);
		}
				
		return salesUtils.mapper(venta, InfoVentaCabDTO.class);
	}

	@Override
	public InfoVentaCabDTO guardarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {
		InfoVentaCab venta = salesUtils.mapper(request, InfoVentaCab.class);
		//guardamos la venta
		InfoVentaCab ventaGuardada;
		try {
			ventaGuardada = infoVentaService.save(venta);	
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar la venta: " + e.getMessage());
		}
		//actualizamos inventario del producto vendido
		restarInventarioPorVenta(request);
				
		return salesUtils.mapper(ventaGuardada, InfoVentaCabDTO.class) ;
	}

	@Override
	public void actualizarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {		
		InfoVentaCab venta = salesUtils.mapper(request, InfoVentaCab.class);
		infoVentaService.update(venta);
	}

	@Override
	public void eliminarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {
		// se aplica eliminación logica
		request.setEstado(Constantes.ESTADO_INACTIVO);
		actualizarVenta(request);
	}

	@Override
	public List<InfoVentaCabDTO> obtenerTodasLasVentasPor(InfoVentaCabDTO request) throws ExcepcionGenerica {
		InfoVentaCab venta = salesUtils.mapper(request, InfoVentaCab.class);
		List<InfoVentaCab> ventasFiltradas = infoVentaService.getAllBy(venta)
		                .stream()
		                .filter(ventas -> !ventas.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(InfoVentaCab::getIdVenta).reversed())
						.collect(Collectors.toList());

		return salesUtils.mapperList(ventasFiltradas, InfoVentaCabDTO.class);
	}

	@Override
	public void eliminarVentaPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta venta 
			InfoVentaCab ventaExistente = infoVentaService.getById(id);
			
			if(ventaExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("La venta ingresada no existe. Detalle error: "+e.getMessage(), 404);
		}
		
		infoVentaService.deleteById(id);
	}
	
	private void restarInventarioPorVenta(InfoVentaCabDTO venta) throws ExcepcionGenerica {
		List<InfoVentaDetDTO> detalleVenta = venta.getDetalleVenta();
		
		// se recorre cada producto vendido para restarlo del inventario
		for(InfoVentaDetDTO detalle : detalleVenta) {
			InfoInventarioDTO inventarioRequest = new InfoInventarioDTO();
			AdmiProductoDTO producto = detalle.getProducto();
			inventarioRequest.setProducto(producto);
			inventarioRequest.setEstado(Constantes.ESTADO_ACTIVO);
			// se consulta el inventario del producto
			infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceImpl);
			
			InfoInventarioDTO inventarioExistente;
			try {
				inventarioExistente = infoInventarioService.obtenerInventarioPor(inventarioRequest);		
			}catch(Exception e) {
				throw new ExcepcionGenerica("Error en obtener inventario para el producto "+ producto.getNombreProducto() + ". Detalle del error: "+ e.getMessage(), 409);
			}
			
			Integer stock = inventarioExistente.getStockTotal();
			
			//se resta el stock con la cantidad de los productos vendidos
			inventarioExistente.setStockTotal( stock - detalle.getCantidad());
			//se aumenta la cantidad de productos vendidos en el inventario
			inventarioExistente.setCantidadVendidos(inventarioExistente.getCantidadVendidos() + detalle.getCantidad());
			
			//setea valores de auditoria
			inventarioExistente.setUsrUltMod(venta.getUsrCreacion());
			inventarioExistente.setFeUltMod(venta.getFechaVenta());
			inventarioExistente.setIpUltMod(venta.getIpCreacion());
			
			//se actualiza inventario
			infoInventarioService.actualizarInventario(inventarioExistente);
		}
	}

}

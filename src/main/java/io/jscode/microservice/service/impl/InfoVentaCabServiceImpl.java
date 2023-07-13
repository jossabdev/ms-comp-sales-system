package io.jscode.microservice.service.impl;

import java.util.List;

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
		List<InfoVentaCab> ventas = infoVentaService.getAll(); 
		return salesUtils.mapperList(ventas, InfoVentaCabDTO.class);
	}

	@Override
	public InfoVentaCabDTO obtenerVentaPorId(Long id) throws ExcepcionGenerica {
		InfoVentaCab venta = infoVentaService.getById(id);
		return salesUtils.mapper(venta, InfoVentaCabDTO.class);
	}

	@Override
	public InfoVentaCabDTO guardarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {
		InfoVentaCab venta = salesUtils.mapper(request, InfoVentaCab.class);
		//guardamos la venta
		InfoVentaCab ventaGuardada = infoVentaService.save(venta);		
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
		List<InfoVentaCab> ventasFiltradas = infoVentaService.getAllBy(venta);
		return salesUtils.mapperList(ventasFiltradas, InfoVentaCabDTO.class);
	}

	@Override
	public void eliminarVentaPorId(Long id) throws ExcepcionGenerica {
		InfoVentaCabDTO venta = new InfoVentaCabDTO();
		venta.setIdVenta(id);
		eliminarVenta(venta);
	}
	
	private void restarInventarioPorVenta(InfoVentaCabDTO venta) throws ExcepcionGenerica {
		List<InfoVentaDetDTO> detalleVenta = venta.getDetalleVenta();
		
		// se recorre cada producto vendido para restarlo del inventario
		for(InfoVentaDetDTO detalle : detalleVenta) {
			InfoInventarioDTO inventarioRequest = new InfoInventarioDTO();
			AdmiProductoDTO producto = detalle.getProducto();
			inventarioRequest.setProducto(producto);
			
			// se consulta el inventario del producto
			infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceImpl);
			InfoInventarioDTO inventarioExistente = infoInventarioService.obtenerInventarioPor(inventarioRequest);		
			
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

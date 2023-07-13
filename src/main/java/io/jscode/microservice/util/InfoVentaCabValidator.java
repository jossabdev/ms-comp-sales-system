package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.microservice.service.AdmiProductoService;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.InfoVentaCabService;
import io.jscode.microservice.service.impl.AdmiProductoServiceImpl;
import io.jscode.microservice.service.impl.InfoInventarioServiceImpl;
import io.jscode.microservice.service.impl.InfoVentaCabServiceImpl;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class InfoVentaCabValidator {
	
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String infoVentaCabServiceimpl = "InfoVentaCabServiceImpl";
	String admiProductoServiceimpl = "AdmiProductoServiceImpl";
	String infoInventarioServiceImpl = "InfoInventarioServiceImpl";
	
	public InfoVentaCabDTO validarActualizarInfoVenta(InfoVentaCabDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		InfoVentaCabDTO ventaResultante = new InfoVentaCabDTO();
		
		if(request.getIdVenta() == null) {
			throw new ExcepcionGenerica("El parametro idVenta es requerido");
		}
		
		ventaResultante.setIdVenta(request.getIdVenta());
		InfoVentaCabService infoVentaCabService = (InfoVentaCabServiceImpl) beanFactory.getBean(infoVentaCabServiceimpl);
		
		// se valida que exista la venta
		InfoVentaCabDTO ventaExistente = infoVentaCabService.obtenerVentaPorId(request.getIdVenta());
		
		if(ventaExistente == null) {
			throw new ExcepcionGenerica("El objeto venta con idVenta " + request.getIdVenta() + " no existe");
		}
		
		// validacion vendedor
		if(request.getVendedor() == null || request.getVendedor().equals("")) {
			ventaResultante.setVendedor(ventaExistente.getVendedor());
		}else {
			ventaResultante.setVendedor(request.getVendedor());
		}
		
		// validacion cliente
		if(request.getCliente() == null || request.getCliente().equals("")) {
			ventaResultante.setCliente(ventaExistente.getCliente());
		}else {
			ventaResultante.setCliente(request.getCliente());
		}
		
		// validacion subtotal
		if(request.getSubtotal() == null ) {
			ventaResultante.setSubtotal(ventaExistente.getSubtotal());
		}else {
			ventaResultante.setSubtotal(request.getSubtotal());
		}
		
		// validacion iva
		if(request.getIva() == null ) {
			ventaResultante.setIva(ventaExistente.getIva());
		}else {
			ventaResultante.setIva(request.getIva());
		}
		
		// validacion descuento
		if(request.getDescuento() == null ) {
			ventaResultante.setDescuento(ventaExistente.getDescuento());
		}else {
			ventaResultante.setDescuento(request.getDescuento());
		}
		
		// validacion total
		if(request.getTotal() == null ) {
			ventaResultante.setTotal(ventaExistente.getTotal());
		}else {
			ventaResultante.setTotal(request.getTotal());
		}
		
		// validacion fechaVenta
		if(request.getFechaVenta() == null ) {
			ventaResultante.setFechaVenta(ventaExistente.getFechaVenta());
		}else {
			ventaResultante.setFechaVenta(request.getFechaVenta());
		}
		
						
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			ventaResultante.setEstado(ventaExistente.getEstado());
		}else {
			ventaResultante.setEstado(request.getEstado());
		}
		
		// validacion detalle venta
		if(request.getDetalleVenta() == null ) {
			ventaResultante.setDetalleVenta(ventaExistente.getDetalleVenta());
		}else {
			ventaResultante.setDetalleVenta(request.getDetalleVenta());
		}
		
		ventaResultante.setFeCreacion(ventaExistente.getFeCreacion());
		ventaResultante.setUsrCreacion(ventaExistente.getUsrCreacion());
		ventaResultante.setIpCreacion(ventaExistente.getIpCreacion());
		ventaResultante.setFeUltMod(LocalDateTime.now());
		
		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			ventaResultante.setUsrUltMod(usrUltMod);
		}
		ventaResultante.setIpUltMod(salesUtils.getClientIp());
		
		return ventaResultante;
	}
	
	public void validarGuardarVenta(InfoVentaCabDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getVendedor() == null || request.getVendedor().isBlank()) {
			throw new ExcepcionGenerica("El parametro vendedor es requerido");
		}
		
		if(request.getCliente() == null ||request.getCliente().isBlank() ) {
			throw new ExcepcionGenerica("El parametro cliente es requerido");
		}
		
		if(request.getSubtotal() == null) {
			throw new ExcepcionGenerica("El parametro subtotal es requerido");
		}
		
		if(request.getIva() == null) {
			throw new ExcepcionGenerica("El parametro iva es requerido");
		}
		
		if(request.getDescuento() == null) {
			throw new ExcepcionGenerica("El parametro descuento es requerido");
		}
		
		if(request.getTotal() == null) {
			throw new ExcepcionGenerica("El parametro total es requerido");
		}
		
		if(request.getFechaVenta() == null) {
			throw new ExcepcionGenerica("El parametro fechaVenta es requerido");
		}
		
		//validacion detalle venta		
		for(InfoVentaDetDTO detalle : request.getDetalleVenta()) {
			
			// se valida existencia del producto
			AdmiProductoDTO productoRequest  = detalle.getProducto();
			AdmiProductoService admiProductoService = (AdmiProductoServiceImpl) beanFactory.getBean(admiProductoServiceimpl);
			AdmiProductoDTO productoExistente = admiProductoService.obtenerProductoPor(productoRequest);
			
			if(productoExistente == null) {
				throw new ExcepcionGenerica("El producto no existe");
			}
			detalle.setProducto(productoExistente);
			
			// se valida existencia del inventario del producto
			InfoInventarioDTO inventarioRequest = new InfoInventarioDTO();
			inventarioRequest.setProducto(productoExistente);			
			InfoInventarioService infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceImpl);
			InfoInventarioDTO inventarioExistente = infoInventarioService.obtenerInventarioPor(inventarioRequest);
			
			if(inventarioExistente == null) {
				throw new ExcepcionGenerica("No existe inventario para el producto " + productoExistente.getNombreProducto());
			}
			
			// se valida que stock del inventario no sea cero o menor a la cantidad de la venta
			Integer stock = inventarioExistente.getStockTotal();
			
			if(stock.intValue() <= 0 || stock.intValue() < detalle.getCantidad()) {
				throw new ExcepcionGenerica("No se puede vender el producto " + productoExistente.getNombreProducto() + " debido a que no hay stock");
			}
		}
				
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			throw new ExcepcionGenerica("El parametro estado es requerido");
		}
		
		if(request.getUsrCreacion() == null || request.getUsrCreacion().isBlank()) {
			String usrCreacion = headers.get("user");
			
			if(usrCreacion == null || usrCreacion.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			request.setUsrCreacion(usrCreacion);
		}
		
		if(request.getFeCreacion() == null) {
			request.setFeCreacion(LocalDateTime.now());
		}
		
		if(request.getIpCreacion() == null) {
			request.setIpCreacion(salesUtils.getClientIp());
		}
	}
}

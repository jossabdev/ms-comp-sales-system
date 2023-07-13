package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.microservice.service.InfoVentaDetService;
import io.jscode.microservice.service.impl.InfoVentaDetServiceImpl;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class InfoVentaDetValidator {
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String infoVentaDetServiceimpl = "InfoVentaDetServiceImpl";
	
	public InfoVentaDetDTO validarActualizarInfoVentaDet(InfoVentaDetDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		InfoVentaDetDTO detalleVentaResultante = new InfoVentaDetDTO();
		
		if(request.getIdVentaDet() == null) {
			throw new ExcepcionGenerica("El parametro idVentaDet es requerido");
		}
		
		detalleVentaResultante.setIdVentaDet(request.getIdVentaDet());
		InfoVentaDetService infoVentaDetService = (InfoVentaDetServiceImpl) beanFactory.getBean(infoVentaDetServiceimpl);
		
		// se valida que exista la venta detalle
		InfoVentaDetDTO detalleVentaExistente = infoVentaDetService.obtenerDetalleVentaPorId(request.getIdVentaDet());
		
		if(detalleVentaExistente == null) {
			throw new ExcepcionGenerica("El objeto detalleVenta con idVentaDet " + request.getIdVentaDet() + " no existe");
		}
		
		// validacion venta
		if(request.getVenta() == null || request.getVenta().getIdVenta() == null) {
			detalleVentaResultante.setVenta(detalleVentaExistente.getVenta());
		}else {
			detalleVentaResultante.setVenta(request.getVenta());
		}
		
		// validacion producto
		if(request.getProducto() == null || request.getProducto().getIdProducto() == null) {
			detalleVentaResultante.setProducto(detalleVentaExistente.getProducto());
		}else {
			detalleVentaResultante.setProducto(request.getProducto());
		}
		
		// validacion producto
		if(request.getProducto() == null || request.getProducto().getIdProducto() == null) {
			detalleVentaResultante.setProducto(detalleVentaExistente.getProducto());
		}else {
			detalleVentaResultante.setProducto(request.getProducto());
		}
				
		// validacion cantidad
		if(request.getCantidad() == null) {
			detalleVentaResultante.setCantidad(detalleVentaExistente.getCantidad());
		}else {
			detalleVentaResultante.setCantidad(request.getCantidad());
		}
		
		// validacion valorUnitario
		if(request.getValorUnitario() == null) {
			detalleVentaResultante.setValorUnitario(detalleVentaExistente.getValorUnitario());
		}else {
			detalleVentaResultante.setValorUnitario(request.getValorUnitario());
		}
		
		// validacion valorTotal
		if(request.getValorTotal() == null) {
			detalleVentaResultante.setValorTotal(detalleVentaExistente.getValorTotal());
		}else {
			detalleVentaResultante.setValorTotal(request.getValorTotal());
		}
						
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			detalleVentaResultante.setEstado(detalleVentaExistente.getEstado());
		}else {
			detalleVentaResultante.setEstado(request.getEstado());
		}

		
		detalleVentaResultante.setFeCreacion(detalleVentaExistente.getFeCreacion());
		detalleVentaResultante.setUsrCreacion(detalleVentaExistente.getUsrCreacion());
		detalleVentaResultante.setIpCreacion(detalleVentaExistente.getIpCreacion());
		detalleVentaResultante.setFeUltMod(LocalDateTime.now());
		
		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			detalleVentaResultante.setUsrUltMod(usrUltMod);
		}
		detalleVentaResultante.setIpUltMod(salesUtils.getClientIp());
		
		return detalleVentaResultante;
	}
	
	public void validarGuardarDetalleVenta(InfoVentaDetDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getVenta() == null || request.getVenta().getIdVenta() == null) {
			throw new ExcepcionGenerica("El parametro venta es requerido");
		}
		
		if(request.getProducto() == null ||request.getProducto().getIdProducto() == null ) {
			throw new ExcepcionGenerica("El parametro prducto es requerido");
		}
		
		if(request.getCantidad() == null) {
			throw new ExcepcionGenerica("El parametro cantidad es requerido");
		}
		
		if(request.getValorUnitario() == null) {
			throw new ExcepcionGenerica("El parametro valorUnitario es requerido");
		}
		
		if(request.getValorTotal() == null) {
			throw new ExcepcionGenerica("El parametro valorTotal es requerido");
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

package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.impl.InfoInventarioServiceImpl;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class InfoInventarioValidator {
	
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String infoInventarioServiceimpl = "InfoInventarioServiceImpl";
	
	public InfoInventarioDTO validarActualizarInventario (InfoInventarioDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		InfoInventarioDTO inventarioResultante = new InfoInventarioDTO();
		
		if(request.getIdInventario() == null) {
			throw new ExcepcionGenerica("El parametro idInventario es requerido");
		}
		
		inventarioResultante.setIdInventario(request.getIdInventario());
		InfoInventarioService infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceimpl);
		
		// se valida que exista el inventario
		InfoInventarioDTO inventarioExistente = infoInventarioService.obtenerInventarioPorId(request.getIdInventario());
		
		if(inventarioExistente == null) {
			throw new ExcepcionGenerica("El objeto inventario con idInventario " + request.getIdInventario() + " no existe");
		}
		
		// validacion producto
		if(request.getProducto() == null || request.getProducto().getIdProducto() == null) {
			inventarioResultante.setProducto(inventarioExistente.getProducto());
		}else {
			inventarioResultante.setProducto(request.getProducto());
		}
		
		// validacion stock inicial
		if(request.getStockInicial() == null || request.getStockInicial().equals("")) {
			inventarioResultante.setStockInicial(inventarioExistente.getStockInicial());
		}else {
			inventarioResultante.setStockInicial(request.getStockInicial());
		}
		
		// validacion cantidad ingresados
		if(request.getCantidadIngresados() == null) {
			inventarioResultante.setCantidadIngresados(inventarioExistente.getCantidadIngresados());
		}else {
			inventarioResultante.setCantidadIngresados(request.getCantidadIngresados());
		}
		
		// validacion cantidad vendidos
		if(request.getCantidadVendidos() == null) {
			inventarioResultante.setCantidadVendidos(inventarioExistente.getCantidadVendidos());
		}else {
			inventarioResultante.setCantidadVendidos(request.getCantidadVendidos());
		}
		
		// validacion stock total
		if(request.getStockTotal() == null ) {
			inventarioResultante.setStockTotal(inventarioExistente.getStockTotal());
		}else {
			inventarioResultante.setStockTotal(request.getStockTotal());
		}
						
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			inventarioResultante.setEstado(inventarioExistente.getEstado());
		}else {
			inventarioResultante.setEstado(request.getEstado());
		}
		
		inventarioResultante.setFeCreacion(inventarioExistente.getFeCreacion());
		inventarioResultante.setUsrCreacion(inventarioExistente.getUsrCreacion());
		inventarioResultante.setIpCreacion(inventarioExistente.getIpCreacion());
		inventarioResultante.setFeUltMod(LocalDateTime.now());
		
		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			inventarioResultante.setUsrUltMod(usrUltMod);
		}
		inventarioResultante.setIpUltMod(salesUtils.getClientIp());
		
		return inventarioResultante;
	}
	
	public void validarGuardarInventario(InfoInventarioDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getProducto() == null || request.getProducto().getIdProducto() == null) {
			throw new ExcepcionGenerica("El parametro producto es requerido");
		}
		
		if(request.getStockInicial() == null) {
			throw new ExcepcionGenerica("El parametro stockInicial es requerido");
		}
		
		if(request.getStockTotal() == null) {
			throw new ExcepcionGenerica("El parametro stockTotal es requerido");
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

package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.impl.InfoInventarioServiceImpl;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class InfoInventarioValidator {
	
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String infoInventarioServiceimpl = "InfoInventarioServiceImpl";

	String admiProductoServiceImpl = "AdmiProductoServiceImpl";
	
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
		if(request.getStockInicial() == null) {
			inventarioResultante.setStockInicial(inventarioExistente.getStockInicial());
		}else {
			inventarioResultante.setStockInicial(request.getStockInicial());
		}
		
		Integer cantidadIngresados = 0;
		// validacion cantidad ingresados
		if(request.getCantidadIngresados() == null) {
			inventarioResultante.setCantidadIngresados(inventarioExistente.getCantidadIngresados());
		}else {
			cantidadIngresados = request.getCantidadIngresados();
			
			if(cantidadIngresados > 0) {
				Integer sumaCantidadIngresados = cantidadIngresados + inventarioExistente.getCantidadIngresados();
				inventarioResultante.setCantidadIngresados(sumaCantidadIngresados);
			}else {
				inventarioResultante.setCantidadIngresados(inventarioExistente.getCantidadIngresados());
			}
		}
		
		// validacion cantidad vendidos
		if(request.getCantidadVendidos() == null) {
			inventarioResultante.setCantidadVendidos(inventarioExistente.getCantidadVendidos());
		}else {
			inventarioResultante.setCantidadVendidos(request.getCantidadVendidos());
		}
		
		// validacion stock total
		if(request.getStockTotal() == null ) {
			// si stock total llega nulo, se valida que haya cantidad ingresados para sumarlo al stock existente
			if(cantidadIngresados > 0) {
				Integer sumaStockTotal = inventarioExistente.getStockTotal() + cantidadIngresados;
				inventarioResultante.setStockTotal(sumaStockTotal);
			}else {
				inventarioResultante.setStockTotal(inventarioExistente.getStockTotal());
			}
		}else {
			if(cantidadIngresados > 0 ) {
				Integer sumaStockTotal = inventarioExistente.getStockTotal() + cantidadIngresados;
				if(!sumaStockTotal.equals(request.getStockTotal())) {
					throw new ExcepcionGenerica("El valor del stockTotal no coincide con la sumatoria entre la cantidad ingresada y el stock actual");
				}else {
					inventarioResultante.setStockTotal(request.getStockTotal());
				}
			}else {
				inventarioResultante.setStockTotal(request.getStockTotal());
			}
			
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

		// se valida que no exista otro inventario con mismo producto (mismo productoId)
		InfoInventarioService infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceimpl);
		
		// se valida que exista el inventario del producto 
		InfoInventarioDTO inventarioPorProducto = new InfoInventarioDTO();
		inventarioPorProducto.setProducto(request.getProducto());
		try{
			List<InfoInventarioDTO> inventariosExistentes = infoInventarioService.obtenerTodosLosInventariosPor(inventarioPorProducto);

			if(inventariosExistentes.size() > 0 ){
				for(InfoInventarioDTO inventarioExistente: inventariosExistentes){
					if(inventarioExistente.getIdInventario() != null && inventarioExistente.getProducto() != null && inventarioExistente.getEstado().equals("Activo")){
						throw new ExcepcionGenerica("Inventario ya existe");
					}
				}			
			}
			
		}catch(ExcepcionGenerica e){			
			throw new ExcepcionGenerica("No es posible crear el inventario. Error técnico: " + e.getMessage());
		}
	}
	
	public InfoInventarioDTO validarEliminarInventario(InfoInventarioDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		
		if(request.getIdInventario() == null && request.getProducto() == null) {
			throw new ExcepcionGenerica("El parametro idInventario o producto es requerido");
		}
	
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			request.setEstado(Constantes.ESTADO_ACTIVO);
		}
		
		// se consulta inventario por filtro
		InfoInventarioService infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(infoInventarioServiceimpl);
		InfoInventarioDTO inventarioExistente = new InfoInventarioDTO();
		try {
			InfoInventarioDTO inventarioRequest = new InfoInventarioDTO();
			inventarioRequest.setIdInventario(request.getIdInventario());

			List<InfoInventarioDTO> inventariosExistentes = infoInventarioService.obtenerTodosLosInventariosPor(inventarioRequest);
			Optional<InfoInventarioDTO> inventarioOpt = inventariosExistentes.stream()
			    .filter(inventarioTmp -> !inventarioTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
			    .findFirst();
			
			if(inventarioOpt.isPresent()){
				inventarioExistente = inventarioOpt.get();
				inventarioExistente.setFeUltMod(LocalDateTime.now());

				if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
					String usrUltMod = headers.get("user");
					
					if(usrUltMod == null || usrUltMod.isBlank()) {
						throw new ExcepcionGenerica("El parametro header user es requerido");
					}
					inventarioExistente.setUsrUltMod(usrUltMod);
				}
				inventarioExistente.setIpUltMod(salesUtils.getClientIp());
			}
			
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			throw new ExcepcionGenerica("El inventario ingresado no existe. Detalle de error: " + e.getMessage(), 404);
		}
		return inventarioExistente;
	}
}

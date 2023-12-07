package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.AdmiProductoDTO;
import io.jscode.microservice.dto.InfoInventarioDTO;
import io.jscode.microservice.service.AdmiProductoService;
import io.jscode.microservice.service.InfoInventarioService;
import io.jscode.microservice.service.impl.AdmiProductoServiceImpl;
import io.jscode.microservice.service.impl.InfoInventarioServiceImpl;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class AdmiProductoValidator {

	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String admiProductoServiceimpl = "AdmiProductoServiceImpl";
	
	public AdmiProductoDTO validarActualizarProducto(AdmiProductoDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		AdmiProductoDTO productoResultante = new AdmiProductoDTO();
		
		if(request.getIdProducto() == null) {
			throw new ExcepcionGenerica("El parametro idProducto es requerido", 422);
		}
		productoResultante.setIdProducto(request.getIdProducto());
		AdmiProductoService admiProductoService = (AdmiProductoServiceImpl) beanFactory.getBean(admiProductoServiceimpl);
		AdmiProductoDTO productoExistente = admiProductoService.obtenerProductoPorId(request.getIdProducto());
		
		if(productoExistente == null) {
			throw new ExcepcionGenerica("El objeto producto con idProducto " + request.getIdProducto() + " no existe", 404);
		}
		
		// validacion nombre producto
		if(request.getNombreProducto() == null || request.getNombreProducto().isBlank()) {
			productoResultante.setNombreProducto(productoExistente.getNombreProducto());
		}else {
			productoResultante.setNombreProducto(request.getNombreProducto());
		}
		
		// validacion descripcion
		if(request.getDescripcion() == null || request.getDescripcion().isBlank()) {
			productoResultante.setDescripcion(productoExistente.getDescripcion());
		}else {
			productoResultante.setDescripcion(request.getDescripcion());
		}
		
		// validacion codigo de barras
		if(request.getCodigoBarras() == null || request.getCodigoBarras().isBlank()) {
			productoResultante.setCodigoBarras(productoExistente.getCodigoBarras());
		}else {
			productoResultante.setCodigoBarras(request.getCodigoBarras());
		}
		
		// validacion de categoria
		if(request.getCategoria() == null || request.getCategoria().getIdCategoria() == null ) {
			productoResultante.setCategoria(productoExistente.getCategoria());
		}else {		
			productoResultante.setCategoria(request.getCategoria());
		}
		
		// validacion costo
		if(request.getCosto() == null ) {
			productoResultante.setCosto(productoExistente.getCosto());
		}else {
			productoResultante.setCosto(request.getCosto());
		}
		// validacion precio
		if(request.getPrecioVenta() == null ) {
			productoResultante.setPrecioVenta(productoExistente.getPrecioVenta());
		}else {
			productoResultante.setPrecioVenta(request.getPrecioVenta());
		}
		
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			productoResultante.setEstado(productoExistente.getEstado());
		}else {
			productoResultante.setEstado(request.getEstado());
		}
		
		productoResultante.setFeCreacion(productoExistente.getFeCreacion());
		productoResultante.setUsrCreacion(productoExistente.getUsrCreacion());
		productoResultante.setIpCreacion(productoExistente.getIpCreacion());
		productoResultante.setFeUltMod(LocalDateTime.now());
		
		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			productoResultante.setUsrUltMod(usrUltMod);
		}
		productoResultante.setIpUltMod(salesUtils.getClientIp());
		
		return productoResultante;
	}
	
	public void validarGuardarProducto(AdmiProductoDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getNombreProducto() == null || request.getNombreProducto().isBlank()) {
			throw new ExcepcionGenerica("El parametro nombreProducto es requerido", 422);
		}
		
		if(request.getCategoria() == null || request.getCategoria().getIdCategoria() == null) {
			throw new ExcepcionGenerica("El parametro categoria es requerido", 422);
		}
		
		if(request.getCosto() == null) {
			throw new ExcepcionGenerica("El parametro costo es requerido", 422);
		}
		
		if(request.getPrecioVenta() == null) {
			throw new ExcepcionGenerica("El parametro precioVenta es requerido", 422);
		}
		
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			throw new ExcepcionGenerica("El parametro estado es requerido", 422);
		}
		
		if(request.getUsrCreacion() == null || request.getUsrCreacion().isBlank()) {
			String usrCreacion = headers.get("user");
			
			if(usrCreacion == null || usrCreacion.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido", 422);
			}
			request.setUsrCreacion(usrCreacion);
		}
		
		if(request.getFeCreacion() == null) {
			request.setFeCreacion(LocalDateTime.now());
		}
		
		if(request.getIpCreacion() == null) {
			request.setIpCreacion(salesUtils.getClientIp());
		}

		// validar si producto existe con mismo codigo de barras
		if(!request.getCodigoBarras().isEmpty()){
			AdmiProductoService admiProductoService = (AdmiProductoServiceImpl) beanFactory.getBean(admiProductoServiceimpl);
			AdmiProductoDTO productoRequest = new AdmiProductoDTO();
			productoRequest.setCodigoBarras(request.getCodigoBarras());
			
			List<AdmiProductoDTO> productosTmp = admiProductoService.obtenerTodosLosProductosPor(productoRequest);
			
			Optional<AdmiProductoDTO> productoOpt = productosTmp.stream()
				.filter(productoTmp -> !productoTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
				.findFirst();
			
			if(productoOpt.isPresent()){
				AdmiProductoDTO productoExistente = productoOpt.get();

				if(productoExistente != null && productoExistente.getIdProducto() != null && (productoExistente.getEstado().equals("Activo") ||  productoExistente.getEstado().equals("Modificado"))) {
					throw new ExcepcionGenerica("Ya existe producto con código de barra " + request.getCodigoBarras(), 409);
				}
			}
		}
		
		
	}
	
	public AdmiProductoDTO validarEliminarProducto(AdmiProductoDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		AdmiProductoService admiProductoService;
		InfoInventarioService infoInventarioService;		
		AdmiProductoDTO productoExistente;
		
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			request.setEstado(Constantes.ESTADO_ACTIVO);
		}
		
		// se consulta producto por filtro
		admiProductoService = (AdmiProductoServiceImpl) beanFactory.getBean(admiProductoServiceimpl);
		try {
			productoExistente = admiProductoService.obtenerProductoPor(request);
			
			
			// compruebo que el producto no tenga un inventario
			infoInventarioService = (InfoInventarioServiceImpl) beanFactory.getBean(InfoInventarioServiceImpl.class);

			InfoInventarioDTO inventarioRequest = new InfoInventarioDTO();
			inventarioRequest.setProducto(productoExistente);
			List<InfoInventarioDTO> inventariosTmp = infoInventarioService.obtenerTodosLosInventariosPor(inventarioRequest);
			Optional<InfoInventarioDTO> inventarioOpt = inventariosTmp.stream()
			    .filter(inventarioTmp -> !inventarioTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
			    .findFirst();
			
			if(inventarioOpt.isPresent()){
				throw new ExcepcionGenerica("El producto no puede eliminarse debido a que tiene inventario");
			}

			productoExistente.setFeUltMod(LocalDateTime.now());

			if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
				String usrUltMod = headers.get("user");
				
				if(usrUltMod == null || usrUltMod.isBlank()) {
					throw new ExcepcionGenerica("El parametro header user es requerido");
				}
				productoExistente.setUsrUltMod(usrUltMod);
			}
			productoExistente.setIpUltMod(salesUtils.getClientIp());
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			throw new ExcepcionGenerica("El producto ingresado no existe. Detalle de error: " + e.getMessage(), 404);
		}

		
		return productoExistente;
	}
}

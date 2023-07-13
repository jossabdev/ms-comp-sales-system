package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.AdmiCategoriaDTO;
import io.jscode.microservice.service.AdmiCategoriaService;
import io.jscode.microservice.service.impl.AdmiCategoriaServiceImpl;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class AdmiCategoriaValidator {
		
	@Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String admiCategoriaServiceimpl = "AdmiCategoriaServiceImpl";
	
	public AdmiCategoriaDTO validarActualizarCategoria(AdmiCategoriaDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		
		AdmiCategoriaService admiCategoriaService;
		AdmiCategoriaDTO categoriaResultante = new AdmiCategoriaDTO();
		
		if (request.getIdCategoria() == null) {
			throw new ExcepcionGenerica("El parametro idCategoria es requerido", 422);
		}
		admiCategoriaService = (AdmiCategoriaServiceImpl) beanFactory.getBean(admiCategoriaServiceimpl);
		AdmiCategoriaDTO categoriaExistente = admiCategoriaService.obtenerCategoriaPorId(request.getIdCategoria());
		
		if(categoriaExistente == null) {
			throw new ExcepcionGenerica("El objeto categoria con categoriaId " + request.getIdCategoria() + " no existe", 404);
		}
		
		if(categoriaExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
			throw new ExcepcionGenerica("El objeto categoria con categoriaId " + request.getIdCategoria() + " no puede ser modificado por tener estado inactivo");
		}
		
		// idCategoria
		categoriaResultante.setIdCategoria(request.getIdCategoria());
		
		// validacion nombreCategoria
		if(request.getNombreCategoria() == null || request.getNombreCategoria().isBlank()) {
			categoriaResultante.setNombreCategoria(categoriaExistente.getNombreCategoria());
		}else {
			categoriaResultante.setNombreCategoria(request.getNombreCategoria());
		}
		
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			categoriaResultante.setEstado(categoriaExistente.getEstado());
		}else {
			categoriaResultante.setEstado(request.getEstado());
		}
		
		categoriaResultante.setFeCreacion(categoriaExistente.getFeCreacion());
		categoriaResultante.setUsrCreacion(categoriaExistente.getUsrCreacion());
		categoriaResultante.setIpCreacion(categoriaExistente.getIpCreacion());
		categoriaResultante.setFeUltMod(LocalDateTime.now());

		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			categoriaResultante.setUsrUltMod(usrUltMod);
		}
		categoriaResultante.setIpUltMod(salesUtils.getClientIp());
		
		return categoriaResultante;
	}
	
	public AdmiCategoriaDTO validarEliminarCategoria(AdmiCategoriaDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		AdmiCategoriaService admiCategoriaService;		
		AdmiCategoriaDTO categoriaExistente;
		
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			request.setEstado(Constantes.ESTADO_ACTIVO);
		}
		
		// se consulta categoria por filtro
		admiCategoriaService = (AdmiCategoriaServiceImpl) beanFactory.getBean(admiCategoriaServiceimpl);
		try {
			categoriaExistente = admiCategoriaService.obtenerCategoriaPor(request);
			
			categoriaExistente.setFeUltMod(LocalDateTime.now());

			if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
				String usrUltMod = headers.get("user");
				
				if(usrUltMod == null || usrUltMod.isBlank()) {
					throw new ExcepcionGenerica("El parametro header user es requerido");
				}
				categoriaExistente.setUsrUltMod(usrUltMod);
			}
			categoriaExistente.setIpUltMod(salesUtils.getClientIp());
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			throw new ExcepcionGenerica("La categoria ingresada no existe. Detalle de error: " + e.getMessage(), 404);
		}
		
		return categoriaExistente;
	}
	
	public void validarGuardarCategoria(AdmiCategoriaDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getNombreCategoria() == null || request.getNombreCategoria().isBlank()) {
			throw new ExcepcionGenerica("El parametro nombreCategoria es requerido", 422);
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
	}

}

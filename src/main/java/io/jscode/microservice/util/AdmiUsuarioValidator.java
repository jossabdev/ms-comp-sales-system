package io.jscode.microservice.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.AdmiUsuarioDTO;
import io.jscode.microservice.service.AdmiUsuarioService;
import io.jscode.microservice.service.impl.AdmiUsuarioServiceImpl;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Component
public class AdmiUsuarioValidator {
    @Autowired
	BeanFactory beanFactory;
	
	@Autowired
	SalesUtils salesUtils;
	
	String admiUsuarioServiceimpl = "AdmiUsuarioServiceImpl";
	
	public AdmiUsuarioDTO validarActualizarUsuario(AdmiUsuarioDTO request, Map<String, String> headers) throws ExcepcionGenerica {
		AdmiUsuarioDTO usuarioResultante = new AdmiUsuarioDTO();
		
		if(request.getIdUsuario() == null) {
			throw new ExcepcionGenerica("El parametro idUsuario es requerido", 422);
		}
		usuarioResultante.setIdUsuario(request.getIdUsuario());
		AdmiUsuarioService admiUsuarioService = (AdmiUsuarioServiceImpl) beanFactory.getBean(admiUsuarioServiceimpl);
		AdmiUsuarioDTO usuarioExistente = admiUsuarioService.obtenerUsuarioPorId(request.getIdUsuario());
		
		if(usuarioExistente == null) {
			throw new ExcepcionGenerica("El objeto Usuario con idUsuario " + request.getIdUsuario() + " no existe", 404);
		}
		
		// validacion Usuario
		if(request.getUsuario() == null || request.getUsuario().isBlank()) {
			usuarioResultante.setUsuario(usuarioExistente.getUsuario());
		}else {
			usuarioResultante.setUsuario(request.getUsuario());
		}

		// validacion Nombre
		if(request.getNombre() == null || request.getNombre().isBlank()) {
			usuarioResultante.setNombre(usuarioExistente.getNombre());
		}else {
			usuarioResultante.setNombre(request.getNombre());
		}

        // validacion Email
        if(request.getEmail() == null || request.getEmail().isBlank()) {
			usuarioResultante.setEmail(usuarioExistente.getEmail());
		}else {
			usuarioResultante.setEmail(request.getEmail());
		}

        // validacion Rol
        if(request.getRol() == null) {
			usuarioResultante.setRol(usuarioExistente.getRol());
		}else {
			usuarioResultante.setRol(request.getRol());
		}
		
		// validacion password
		if(request.getPassword() == null || request.getPassword().isBlank()) {
			usuarioResultante.setPassword(usuarioExistente.getPassword());
		}else {
			usuarioResultante.setPassword(request.getPassword());
		}
		
		// validacion is disabled
		if(request.isDisabled()&& !usuarioExistente.isDisabled()) {
			usuarioResultante.setDisabled(request.isDisabled());
		}else if(!request.isDisabled() && usuarioExistente.isDisabled()) {
			usuarioResultante.setDisabled(request.isDisabled());
		}
		
		// validacion is locked
		if(request.isLocked() && !usuarioExistente.isLocked()) {
			usuarioResultante.setLocked(request.isLocked());
		}else if(!request.isLocked() && usuarioExistente.isLocked()) {
			usuarioResultante.setLocked(request.isLocked());
		}
		
		
		// validacion estado
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			usuarioResultante.setEstado(usuarioExistente.getEstado());
		}else {
			usuarioResultante.setEstado(request.getEstado());
		}
		
		usuarioResultante.setFeCreacion(usuarioExistente.getFeCreacion());
		usuarioResultante.setUsrCreacion(usuarioExistente.getUsrCreacion());
		usuarioResultante.setIpCreacion(usuarioExistente.getIpCreacion());
		usuarioResultante.setFeUltMod(LocalDateTime.now());
		
		if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
			String usrUltMod = headers.get("user");
			
			if(usrUltMod == null || usrUltMod.isBlank()) {
				throw new ExcepcionGenerica("El parametro header user es requerido");
			}
			usuarioResultante.setUsrUltMod(usrUltMod);
		}
		usuarioResultante.setIpUltMod(salesUtils.getClientIp());
		
		return usuarioResultante;
	}
	
	public void validarGuardarUsuario(AdmiUsuarioDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		if(request.getUsuario() == null || request.getUsuario().isBlank()) {
			throw new ExcepcionGenerica("El parametro usuario es requerido", 422);
		}
		
		if(request.getRol() == null || request.getRol().getIdRol() == null) {
			throw new ExcepcionGenerica("El parametro rol es requerido", 422);
		}
		
		/*if(request.getEmail() == null || request.getEmail().isBlank()) {
			throw new ExcepcionGenerica("El parametro email es requerido", 422);
		}*/
		
		if(request.getPassword() == null || request.getPassword().isBlank()) {
			throw new ExcepcionGenerica("El parametro password es requerido", 422);
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
	
	public AdmiUsuarioDTO validarEliminarUsuario(AdmiUsuarioDTO request, Map<String, String> headers) throws ExcepcionGenerica{
		AdmiUsuarioService admiUsuarioService;		
		AdmiUsuarioDTO usuarioExistente;
		
		if(request.getEstado() == null || request.getEstado().isBlank()) {
			request.setEstado(Constantes.ESTADO_ACTIVO);
		}
		
		// se consulta Usuario por filtro
		admiUsuarioService = (AdmiUsuarioServiceImpl) beanFactory.getBean(admiUsuarioServiceimpl);
		try {
			usuarioExistente = admiUsuarioService.obtenerUsuarioPor(request);
			
			usuarioExistente.setFeUltMod(LocalDateTime.now());

			if(request.getUsrUltMod() == null || request.getUsrUltMod().isBlank()) {
				String usrUltMod = headers.get("user");
				
				if(usrUltMod == null || usrUltMod.isBlank()) {
					throw new ExcepcionGenerica("El parametro header user es requerido");
				}
				usuarioExistente.setUsrUltMod(usrUltMod);
			}
			usuarioExistente.setIpUltMod(salesUtils.getClientIp());
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			throw new ExcepcionGenerica("El Usuario ingresado no existe. Detalle de error: " + e.getMessage(), 404);
		}
		
		return usuarioExistente;
	}
}

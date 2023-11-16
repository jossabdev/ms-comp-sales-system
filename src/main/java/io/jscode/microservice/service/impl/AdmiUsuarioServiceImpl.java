package io.jscode.microservice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiUsuario;
import io.jscode.db.service.DBAdmiUsuarioService;
import io.jscode.microservice.dto.AdmiUsuarioDTO;
import io.jscode.microservice.service.AdmiUsuarioService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("AdmiUsuarioServiceImpl")
public class AdmiUsuarioServiceImpl implements AdmiUsuarioService {

	@Autowired
	DBAdmiUsuarioService admiUsuarioService;

	@Autowired
	SalesUtils salesUtils;

	@Override
	public List<AdmiUsuarioDTO> obtenerTodosLosUsuarios() throws ExcepcionGenerica {
		List<AdmiUsuario> listaUsuarios = admiUsuarioService.getAll();
		return salesUtils.mapperList(listaUsuarios, AdmiUsuarioDTO.class);
	}

	@Override
	public AdmiUsuarioDTO obtenerUsuarioPorId(Long id) throws ExcepcionGenerica {
		AdmiUsuario usuario = admiUsuarioService.getById(id);
		return salesUtils.mapper(usuario, AdmiUsuarioDTO.class);
	}

	@Override
	public AdmiUsuarioDTO obtenerUsuarioPor(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		AdmiUsuario usuarioReq = salesUtils.mapper(request, AdmiUsuario.class);
		AdmiUsuario usuario = admiUsuarioService.getBy(usuarioReq);
		return salesUtils.mapper(usuario, AdmiUsuarioDTO.class);
	}

	@Override
	public AdmiUsuarioDTO guardarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		AdmiUsuario usuario = salesUtils.mapper(request, AdmiUsuario.class);
		AdmiUsuario usuarioGuardado;
		try {
			usuarioGuardado = admiUsuarioService.save(usuario);
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar el usuario: " + e.getMessage());
		}
		return salesUtils.mapper(usuarioGuardado, AdmiUsuarioDTO.class);
	}

	@Override
	public void actualizarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		AdmiUsuario usuario = salesUtils.mapper(request, AdmiUsuario.class);
		admiUsuarioService.update(usuario);		
	}

	@Override
	public void eliminarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// se aplica borrado logico
		request.setEstado(Constantes.ESTADO_ELIMINADO);
		actualizarUsuario(request);
	}

	@Override
	public void eliminarUsuarioPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta usuario 
			AdmiUsuario usuarioExistente = admiUsuarioService.getById(id);
			
			if(usuarioExistente.getEstado().equals(Constantes.ESTADO_ELIMINADO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("El usuario no existe. Detalle error: "+e.getMessage(), 404);
		}
		admiUsuarioService.deleteById(id);
		
	}

	@Override
	public List<AdmiUsuarioDTO> obtenerTodosLosUsuariosPor(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		AdmiUsuario usuario = salesUtils.mapper(request, AdmiUsuario.class);
		List<AdmiUsuario> usuariosFiltrados = admiUsuarioService.getAllBy(usuario)
		                .stream()
		                .filter(usuarioTmp -> !usuarioTmp.getEstado().equals(Constantes.ESTADO_ELIMINADO))
						.sorted(Comparator.comparing(AdmiUsuario::getIdUsuario).thenComparing(AdmiUsuario::getEstado))
						.collect(Collectors.toList());

		return salesUtils.mapperList(usuariosFiltrados, AdmiUsuarioDTO.class);
	}

}

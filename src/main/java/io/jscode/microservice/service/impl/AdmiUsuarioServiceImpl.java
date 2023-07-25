package io.jscode.microservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.jscode.microservice.dto.AdmiUsuarioDTO;
import io.jscode.microservice.service.AdmiUsuarioService;
import io.jscode.util.ExcepcionGenerica;

@Service
public class AdmiUsuarioServiceImpl implements AdmiUsuarioService {

	@Override
	public List<AdmiUsuarioDTO> obtenerTodosLosUsuarios() throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiUsuarioDTO obtenerUsuarioPorId(Long id) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiUsuarioDTO obtenerUsuarioPor(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiUsuarioDTO guardarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void actualizarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarUsuarioPorId(Long id) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdmiUsuarioDTO> obtenerTodosLosUsuariosPor(AdmiUsuarioDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

}

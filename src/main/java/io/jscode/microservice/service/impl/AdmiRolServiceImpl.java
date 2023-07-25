package io.jscode.microservice.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.jscode.microservice.dto.AdmiRolDTO;
import io.jscode.microservice.service.AdmiRolService;
import io.jscode.util.ExcepcionGenerica;

@Service
public class AdmiRolServiceImpl implements AdmiRolService {

	@Override
	public List<AdmiRolDTO> obtenerTodosLosRoles() throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiRolDTO obtenerRolPorId(Long id) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiRolDTO obtenerRolPor(AdmiRolDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdmiRolDTO guardarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void actualizarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarRolPorId(Long id) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdmiRolDTO> obtenerTodosLosRolesPor(AdmiRolDTO request) throws ExcepcionGenerica {
		// TODO Auto-generated method stub
		return null;
	}

}

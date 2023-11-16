package io.jscode.microservice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiRol;
import io.jscode.db.service.DBAdmiRolService;
import io.jscode.microservice.dto.AdmiRolDTO;
import io.jscode.microservice.service.AdmiRolService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("AdmiRolServiceImpl")
public class AdmiRolServiceImpl implements AdmiRolService {

	@Autowired
	DBAdmiRolService admiRolService;

	@Autowired
	SalesUtils salesUtils;

	@Override
	public List<AdmiRolDTO> obtenerTodosLosRoles() throws ExcepcionGenerica {
		List<AdmiRol> listaRoles = admiRolService.getAll()
		                .stream()
		                .filter(rolTmp -> !rolTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiRol::getIdRol).thenComparing(AdmiRol::getEstado))
						.collect(Collectors.toList());
		return salesUtils.mapperList(listaRoles, AdmiRolDTO.class);
	}

	@Override
	public AdmiRolDTO obtenerRolPorId(Long id) throws ExcepcionGenerica {
		AdmiRol rol = null;
		try {
			rol = admiRolService.getById(id);
		}
		catch(NoSuchElementException e) {
			throw new ExcepcionGenerica("El rol con id "+ id + " no existe. Detalle de error: "+ e.getMessage(), 404);
		}
		return salesUtils.mapper(rol, AdmiRolDTO.class);
	}

	@Override
	public AdmiRolDTO obtenerRolPor(AdmiRolDTO request) throws ExcepcionGenerica {
		AdmiRol rolRequest = salesUtils.mapper(request, AdmiRol.class);
		AdmiRol rol = admiRolService.getBy(rolRequest);
		return salesUtils.mapper(rol, AdmiRolDTO.class);
	}

	@Override
	public AdmiRolDTO guardarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		AdmiRol rol = salesUtils.mapper(request, AdmiRol.class);
		AdmiRol rolGuardado;
		try {
			rolGuardado = admiRolService.save(rol);
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar el rol: " + e.getMessage());
		}
		return salesUtils.mapper(rolGuardado, AdmiRolDTO.class);
	}

	@Override
	public void actualizarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		AdmiRol rol = salesUtils.mapper(request,AdmiRol.class);
		admiRolService.update(rol);
	}

	@Override
	public void eliminarRol(AdmiRolDTO request) throws ExcepcionGenerica {
		// se aplica borrado logico
		request.setEstado(Constantes.ESTADO_INACTIVO);
        actualizarRol(request);	
	}

	@Override
	public void eliminarRolPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta producto 
			AdmiRol rolExistente = admiRolService.getById(id);
			
			if(rolExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("El rol ingresado no existe. Detalle error: "+e.getMessage(), 404);
		}
		admiRolService.deleteById(id);
	}

	@Override
	public List<AdmiRolDTO> obtenerTodosLosRolesPor(AdmiRolDTO request) throws ExcepcionGenerica {
		AdmiRol rol = salesUtils.mapper(request, AdmiRol.class);
		List<AdmiRol> rolesFiltrados = admiRolService.getAllBy(rol)
		                .stream()
		                .filter(rolTmp -> !rolTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiRol::getIdRol).thenComparing(AdmiRol::getEstado))
						.collect(Collectors.toList());

		return salesUtils.mapperList(rolesFiltrados, AdmiRolDTO.class);
	}

}

package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.AdmiRolDTO;
import io.jscode.util.ExcepcionGenerica;

public interface AdmiRolService {
	List<AdmiRolDTO> obtenerTodosLosRoles() throws ExcepcionGenerica;
	AdmiRolDTO obtenerRolPorId(Long id) throws ExcepcionGenerica;
	AdmiRolDTO obtenerRolPor(AdmiRolDTO request) throws ExcepcionGenerica;
	AdmiRolDTO guardarRol(AdmiRolDTO request) throws ExcepcionGenerica;
	void actualizarRol(AdmiRolDTO request) throws ExcepcionGenerica;
	void eliminarRol(AdmiRolDTO request) throws ExcepcionGenerica;
	void eliminarRolPorId(Long id) throws ExcepcionGenerica;
	List<AdmiRolDTO> obtenerTodosLosRolesPor(AdmiRolDTO request) throws ExcepcionGenerica;
}

package io.jscode.microservice.service;

import java.util.List;

import io.jscode.microservice.dto.AdmiUsuarioDTO;
import io.jscode.util.ExcepcionGenerica;

public interface AdmiUsuarioService {
	List<AdmiUsuarioDTO> obtenerTodosLosUsuarios() throws ExcepcionGenerica;
	AdmiUsuarioDTO obtenerUsuarioPorId(Long id) throws ExcepcionGenerica;
	AdmiUsuarioDTO obtenerUsuarioPor(AdmiUsuarioDTO request) throws ExcepcionGenerica;
	AdmiUsuarioDTO guardarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica;
	void actualizarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica;
	void eliminarUsuario(AdmiUsuarioDTO request) throws ExcepcionGenerica;
	void eliminarUsuarioPorId(Long id) throws ExcepcionGenerica;
	List<AdmiUsuarioDTO> obtenerTodosLosUsuariosPor(AdmiUsuarioDTO request) throws ExcepcionGenerica;
}

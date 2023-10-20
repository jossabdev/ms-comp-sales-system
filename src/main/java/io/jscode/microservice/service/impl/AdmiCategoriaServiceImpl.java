package io.jscode.microservice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiCategoria;
import io.jscode.db.service.DBAdmiCategoriaService;
import io.jscode.microservice.dto.AdmiCategoriaDTO;
import io.jscode.microservice.service.AdmiCategoriaService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;


@Service("AdmiCategoriaServiceImpl")
public class AdmiCategoriaServiceImpl implements AdmiCategoriaService{

	@Autowired
	DBAdmiCategoriaService admiCategoriaService;
	
	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public List<AdmiCategoriaDTO> obtenerTodasLasCategorias() throws ExcepcionGenerica{
		List<AdmiCategoria> categorias = admiCategoriaService.getAll()
		                .stream()
						.filter(categoria -> !categoria.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiCategoria::getIdCategoria).thenComparing(AdmiCategoria::getEstado))
						.collect(Collectors.toList());			

		return salesUtils.mapperList(categorias, AdmiCategoriaDTO.class);
	}

	@Override
	public AdmiCategoriaDTO guardarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica {
		
		AdmiCategoria categoria = salesUtils.mapper(request, AdmiCategoria.class);
		AdmiCategoria categoriaGuardada;
		try {
			categoriaGuardada = admiCategoriaService.save(categoria);
		}catch(Exception e) {
			throw new ExcepcionGenerica("Error al guardar categoria. Detalle error: "+e.getMessage());
		}
		return salesUtils.mapper(categoriaGuardada, AdmiCategoriaDTO.class);
	}

	@Override
	public AdmiCategoriaDTO obtenerCategoriaPorId(Long id) throws ExcepcionGenerica {
		AdmiCategoria categoria;
		try {
			categoria = admiCategoriaService.getById(id);
		}catch(Exception e) {
			throw new ExcepcionGenerica("Categotia no existe. Detalle error: "+e.getMessage(), 404);
		}
		return salesUtils.mapper(categoria, AdmiCategoriaDTO.class);
	}

	@Override
	public void actualizarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica {		
		AdmiCategoria categoria = salesUtils.mapper(request, AdmiCategoria.class);
		admiCategoriaService.update(categoria);
	}

	@Override
	public void eliminarCategoria(AdmiCategoriaDTO request) throws ExcepcionGenerica {
		// se aplica borrado logico
		request.setEstado(Constantes.ESTADO_INACTIVO);	
		// se actualiza categoria con estado inactivo
		actualizarCategoria(request);
		
	}

	@Override
	public List<AdmiCategoriaDTO> obtenerTodasLasCategoriasPor(AdmiCategoriaDTO request) throws ExcepcionGenerica {
		AdmiCategoria categoria = salesUtils.mapper(request, AdmiCategoria.class);
		List<AdmiCategoria> categoriasFiltradas = admiCategoriaService.getAllBy(categoria)
		                .stream()
		                .filter(categoriaTmp -> !categoriaTmp.getEstado().equals(Constantes.ESTADO_INACTIVO))
						.sorted(Comparator.comparing(AdmiCategoria::getIdCategoria).thenComparing(AdmiCategoria::getEstado))
						.collect(Collectors.toList()); 

		return salesUtils.mapperList(categoriasFiltradas, AdmiCategoriaDTO.class);
	}

	@Override
	public void eliminarCategoriaPorId(Long id) throws ExcepcionGenerica {
		try{
			// se cosulta la categoria
			AdmiCategoria categoriaExistente = admiCategoriaService.getById(id);
			if(categoriaExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not Found");
			}
		}catch(Exception e) {
			throw new ExcepcionGenerica("La categoria ingresada no existe. Detalle error: "+e.getMessage(), 404);
		}

		admiCategoriaService.deleteById(id);
	}

	@Override
	public AdmiCategoriaDTO obtenerCategoriaPor(AdmiCategoriaDTO request) throws ExcepcionGenerica {
		AdmiCategoria categoria = salesUtils.mapper(request, AdmiCategoria.class);
		AdmiCategoria categoriaExistente = admiCategoriaService.getBy(categoria);
		return salesUtils.mapper(categoriaExistente, AdmiCategoriaDTO.class);
	}

}

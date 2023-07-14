package io.jscode.microservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.InfoVentaDet;
import io.jscode.db.service.DBInfoVentaDetService;
import io.jscode.microservice.dto.InfoVentaDetDTO;
import io.jscode.microservice.service.InfoVentaDetService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("InfoVentaDetServiceImpl")
public class InfoVentaDetServiceImpl implements InfoVentaDetService {

	@Autowired
	DBInfoVentaDetService infoVentaDetService;
	
	@Autowired
	SalesUtils salesUtils;
	
	@Override
	public List<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentas() throws ExcepcionGenerica {
		List<InfoVentaDet> detallesVentas = infoVentaDetService.getAll();
		return salesUtils.mapperList(detallesVentas, InfoVentaDetDTO.class);
	}

	@Override
	public InfoVentaDetDTO obtenerDetalleVentaPorId(Long id) throws ExcepcionGenerica {
		InfoVentaDet detalleVenta ;
		try {
			detalleVenta = infoVentaDetService.getById(id);	
		}catch(Exception e) {
			throw new ExcepcionGenerica("El detalle venta con id "+ id + " no existe. Detalle de error: "+ e.getMessage(), 404);
		}
		
		return salesUtils.mapper(detalleVenta, InfoVentaDetDTO.class);
	}

	@Override
	public InfoVentaDetDTO guardarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		InfoVentaDet detalleVentaGuardado;
		try {
			detalleVentaGuardado = infoVentaDetService.save(detalleVenta);
		}catch(Exception e) {
			throw new ExcepcionGenerica( "Ha ocurrido un error al guardar el detalle de venta: " + e.getMessage());
		}
		
		return salesUtils.mapper(detalleVentaGuardado, InfoVentaDetDTO.class);
	}

	@Override
	public void actualizarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		infoVentaDetService.update(detalleVenta);

	}

	@Override
	public void eliminarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica {
		request.setEstado(Constantes.ESTADO_INACTIVO);
		actualizarDetalleVenta(request);
	}

	@Override
	public void eliminarDetalleVentaPorId(Long id) throws ExcepcionGenerica {
		try{
			// se consulta detalle venta 
			InfoVentaDet detalleVentaExistente = infoVentaDetService.getById(id);
			
			if(detalleVentaExistente.getEstado().equals(Constantes.ESTADO_INACTIVO)) {
				throw new ExcepcionGenerica("Not found");
			}
			
		}catch(Exception e) {
			throw new ExcepcionGenerica("El detalle de venta ingresado no existe. Detalle error: "+e.getMessage(), 404);
		}
		infoVentaDetService.deleteById(id); 
	}

	@Override
	public List<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentasPor(InfoVentaDetDTO request)
			throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		List<InfoVentaDet> detallesVenta = infoVentaDetService.getAllBy(detalleVenta);
		return salesUtils.mapperList(detallesVenta, InfoVentaDetDTO.class);
	}

	@Override
	public InfoVentaDetDTO obtenerDetalleVentaPor(InfoVentaDetDTO request) throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		InfoVentaDet detalleVentaEncontrado = infoVentaDetService.getBy(detalleVenta);		
		return salesUtils.mapper(detalleVentaEncontrado, InfoVentaDetDTO.class);
	}

}

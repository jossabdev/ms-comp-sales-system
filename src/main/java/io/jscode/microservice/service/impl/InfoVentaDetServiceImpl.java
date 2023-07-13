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
		InfoVentaDet detalleVenta = infoVentaDetService.getById(id);
		return salesUtils.mapper(detalleVenta, InfoVentaDetDTO.class);
	}

	@Override
	public InfoVentaDetDTO guardarDetalleVenta(InfoVentaDetDTO request) throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		InfoVentaDet detalleVentaGuardado = infoVentaDetService.save(detalleVenta);
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
		InfoVentaDetDTO detalleVenta = new InfoVentaDetDTO();
		detalleVenta.setIdVentaDet(id);
		eliminarDetalleVenta(detalleVenta);
	}

	@Override
	public List<InfoVentaDetDTO> obtenerTodosLosDetallesDeLasVentasPor(InfoVentaDetDTO request)
			throws ExcepcionGenerica {
		InfoVentaDet detalleVenta = salesUtils.mapper(request, InfoVentaDet.class);
		List<InfoVentaDet> detallesVenta = infoVentaDetService.getAllBy(detalleVenta);
		return salesUtils.mapperList(detallesVenta, InfoVentaDetDTO.class);
	}

}

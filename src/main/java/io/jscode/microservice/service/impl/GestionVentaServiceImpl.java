package io.jscode.microservice.service.impl;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jscode.db.entity.AdmiProducto;
import io.jscode.db.entity.InfoVentaCab;
import io.jscode.db.entity.InfoVentaDet;
import io.jscode.db.service.DBGestionVentasService;
import io.jscode.db.service.DBInfoVentaCabService;
import io.jscode.microservice.dto.ConsultaVentasDTO;
import io.jscode.microservice.dto.ConsultaVentasReqDTO;
import io.jscode.microservice.dto.EstadisticaVentaDTO;
import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.service.GestionVentaService;
import io.jscode.util.Constantes;
import io.jscode.util.ExcepcionGenerica;
import io.jscode.util.SalesUtils;

@Service("GestionVentaServiceImpl")
public class GestionVentaServiceImpl implements GestionVentaService{

	@Autowired
	DBGestionVentasService gestionVentasservice;
	
	@Autowired
	SalesUtils salesUtils;

	@Autowired
	DBInfoVentaCabService infoVentaCabService;
	
	@Override
	public Long proceRegistrarVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {	
		InfoVentaCab ventaCab = salesUtils.mapper(request, InfoVentaCab.class);
		return gestionVentasservice.proceRegistrarVenta(ventaCab);
	}

	@Override
	public ConsultaVentasDTO obtenerTotalDeVentasPor(ConsultaVentasReqDTO request) throws ExcepcionGenerica {
		ConsultaVentasDTO consultaVentas = new ConsultaVentasDTO();
		List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(request.getFechaDesde(), request.getFechaHasta());

		Double totalVentas = listaVentasPorFecha.stream()
								.filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.mapToDouble(InfoVentaCab::getTotal)
								.reduce(0, (valor1, valor2) -> valor1 + valor2 );
        consultaVentas.setTotalVentas(totalVentas);
		return consultaVentas;
	}

	@Override
	public ConsultaVentasDTO obtenerTotalGananciasPor(ConsultaVentasReqDTO request) throws ExcepcionGenerica {
		ConsultaVentasDTO consultaVentas = new ConsultaVentasDTO();
		List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(request.getFechaDesde(), request.getFechaHasta());
		List<InfoVentaCab> listaVentasPorFechaFiltrada = listaVentasPorFecha.stream()
		                        .filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.collect(Collectors.toList());

		Double totalVentas = listaVentasPorFechaFiltrada.stream()
		                        .filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.mapToDouble(InfoVentaCab::getTotal)
								.reduce(0, (valor1, valor2) -> valor1 + valor2 );

		Double costoTotalVentas = 0.0;

        for(InfoVentaCab venta : listaVentasPorFechaFiltrada){			
			List<InfoVentaDet> detalleVenta = venta.getDetalleVenta();
			
			for(InfoVentaDet detalle: detalleVenta){
				if(!detalle.getEstado().equals(Constantes.ESTADO_INACTIVO)){
					Double costoTotalDelDetalle = detalle.getCantidad() * detalle.getProducto().getCosto();
					costoTotalVentas += costoTotalDelDetalle;
				}				
			}
		}	
		Double ganancia = totalVentas - costoTotalVentas;
        consultaVentas.setTotalGanancias(ganancia);
		return consultaVentas;
	}

	@Override
	public ConsultaVentasDTO obtenerProductoTop(ConsultaVentasReqDTO request) throws ExcepcionGenerica {
		ConsultaVentasDTO consultaVentas = new ConsultaVentasDTO();
		List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(request.getFechaDesde(), request.getFechaHasta());
		List<InfoVentaCab> listaVentasPorFechaFiltrada = listaVentasPorFecha.stream()
		                        .filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.collect(Collectors.toList());
		
		List<AdmiProducto> listaProductos = new ArrayList<>();

		for(InfoVentaCab venta : listaVentasPorFechaFiltrada){
			for(InfoVentaDet detalle: venta.getDetalleVenta()){
				if(!detalle.getEstado().equals(Constantes.ESTADO_ANULADO)){
					listaProductos.add(detalle.getProducto());
				}
			}
		}

		Map<String, Long> conteoDeProductos = listaProductos.stream()
		                        .collect(Collectors.groupingBy(AdmiProducto::getNombreProducto, Collectors.counting()));

        String productoTop = "";
		long countInicial = 0;

		for(Map.Entry<String, Long> entry : conteoDeProductos.entrySet()){
			String productoTmp = entry.getKey();
			Long count = entry.getValue();

			if(countInicial < count){
				countInicial = count;
				productoTop = productoTmp;
			}
		}

        consultaVentas.setProductoTop(productoTop);
		return consultaVentas;
	}

	@Override
	public EstadisticaVentaDTO obtenerEstadisticasVentasPorOpcion(Integer opcion) throws ExcepcionGenerica{
		EstadisticaVentaDTO estadisticas = new EstadisticaVentaDTO();
		switch (opcion) {
			case Constantes.OPCION_6_MESES:
				estadisticas = obtenerEstadisticaVentaSeisMeses();
				break;
			case Constantes.OPCION_30_DIAS:
				estadisticas = obtenerEstadisticaVentaPorDias(30);
				break;
			case Constantes.OPCION_7_DIAS:
				estadisticas = obtenerEstadisticaVentaPorDias(7);
				break;
			default:
				break;
		}
		return estadisticas;
	}

	private EstadisticaVentaDTO obtenerEstadisticaVentaSeisMeses() throws ExcepcionGenerica{
		LocalDateTime fechaActual = LocalDateTime.now();		
		LocalDateTime fechaSeisMesesAtras = fechaActual.minusMonths(6);
        List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(fechaSeisMesesAtras, fechaActual);

		// se agrupa por mes
		LinkedHashMap<Month, List<InfoVentaCab>> ventasAgrupadaPorMes = listaVentasPorFecha
		                                  .stream()
										  .sorted(Comparator.comparing(InfoVentaCab::getFechaVenta))
										  .collect(Collectors.groupingBy(t -> t.getFechaVenta().getMonth(), LinkedHashMap::new, Collectors.toList()));
		
		

		List<String> labels = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		ventasAgrupadaPorMes.forEach((mes, ventasPorMes) -> {
			labels.add(mes.name());

			Double totalVentas = ventasPorMes.stream()
								.filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.mapToDouble(InfoVentaCab::getTotal)
								.reduce(0, (valor1, valor2) -> valor1 + valor2 );

            values.add(totalVentas);
		});

		EstadisticaVentaDTO estadisticasSeisMeses = new EstadisticaVentaDTO();
		estadisticasSeisMeses.setLabels(labels);
		estadisticasSeisMeses.setValues(values);

		return estadisticasSeisMeses;
	}

	private EstadisticaVentaDTO obtenerEstadisticaVentaPorDias(int dias) throws ExcepcionGenerica{
		LocalDateTime fechaActual = LocalDateTime.now();		
		LocalDateTime fechaDiasAtras = fechaActual.minusDays(dias);
        List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(fechaDiasAtras, fechaActual);
		
		Map<String, List<InfoVentaCab>> ventasAgrupadaPorDias = listaVentasPorFecha
		                                  .stream()
										  .sorted(Comparator.comparing(InfoVentaCab::getFechaVenta))
										  .collect(Collectors.groupingBy(t -> t.getFechaVenta().getDayOfMonth() + " " + t.getFechaVenta().getMonth().name(), LinkedHashMap::new, Collectors.toList()));

		List<String> labels = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		ventasAgrupadaPorDias.forEach((dia, ventasPordia) -> {
			labels.add(dia);

			Double totalVentas = ventasPordia.stream()
								.filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.mapToDouble(InfoVentaCab::getTotal)
								.reduce(0, (valor1, valor2) -> valor1 + valor2 );

            values.add(totalVentas);
		});

		EstadisticaVentaDTO estadisticasdias = new EstadisticaVentaDTO();
		estadisticasdias.setLabels(labels);
		estadisticasdias.setValues(values);

		return estadisticasdias;
	}

	@Override
	public List<InfoVentaCabDTO> obtenerVentasPorRangoFecha(ConsultaVentasReqDTO request) throws ExcepcionGenerica{
		List<InfoVentaCab> listaVentasPorFecha = gestionVentasservice.consultarVentasPorRangoFecha(request.getFechaDesde(), request.getFechaHasta());

		List<InfoVentaCab> listaVentasFiltrada = listaVentasPorFecha.stream()
								.filter(venta -> !venta.getEstado().equals(Constantes.ESTADO_ANULADO))
								.collect(Collectors.toList());

		return salesUtils.mapperList(listaVentasFiltrada, InfoVentaCabDTO.class);
	}

	@Override
	public void proceAnularVenta(InfoVentaCabDTO request) throws ExcepcionGenerica {
		InfoVentaCab ventaCab = salesUtils.mapper(request, InfoVentaCab.class);
		gestionVentasservice.proceAnularVenta(ventaCab);
	}
}

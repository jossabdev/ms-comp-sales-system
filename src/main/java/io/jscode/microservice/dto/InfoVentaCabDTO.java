package io.jscode.microservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InfoVentaCabDTO {
	
	@JsonProperty("idVenta")
	@JsonAlias("id_venta")
	private Long idVenta;
	
	@JsonProperty("vendedor")
	@JsonAlias("vendedor")
	private String vendedor;
	
	@JsonProperty("cliente")
	@JsonAlias("cliente")
	private String cliente;
	
	@JsonProperty("subtotal")
	@JsonAlias("subtotal")
	private Double subtotal;
	
	@JsonProperty("iva")
	@JsonAlias("iva")
	private Double iva;
	
	@JsonProperty("descuento")
	@JsonAlias("descuento")
	private Double descuento;
	
	@JsonProperty("total")
	@JsonAlias("total")
	private Double total;
	
	@JsonProperty("fechaVenta")
	@JsonAlias("fechaVenta")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaVenta;
	
	@JsonProperty("estado")
	@JsonAlias("estado")
	private String estado;
	
	@JsonProperty("usrCreacion")
	@JsonAlias("usr_creacion")
	private String usrCreacion;
	
	@JsonProperty("feCreacion")
	@JsonAlias("fe_creacion")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime feCreacion;
	
	@JsonProperty("ipCreacion")
	@JsonAlias("ip_creacion")
	private String ipCreacion;
	
	@JsonProperty("usrUltMod")
	@JsonAlias("usr_ult_mod")
	private String usrUltMod;
	
	@JsonProperty("feUltMod")
	@JsonAlias("fe_ult_mod")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime feUltMod;
	
	@JsonProperty("ipUltMod")
	@JsonAlias("ip_ult_mod")
	private String ipUltMod;
	
	@JsonProperty("detalleVenta")
	private List<InfoVentaDetDTO> detalleVenta;
	
}

package io.jscode.microservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InfoInventarioDTO {

	@JsonProperty("idInventario")
	@JsonAlias("id_inventario")
	private Long idInventario;
	
	@JsonProperty("producto")
	@JsonAlias( {"producto_id", "productoId"})
	private AdmiProductoDTO producto;
	
	@JsonProperty("stockInicial")
	@JsonAlias("stock_inicial")
	private Integer stockInicial;
	
	@JsonProperty("cantidadIngresados")
	@JsonAlias("cantidad_ingresados")
	private Integer cantidadIngresados;
	
	@JsonProperty("cantidadVendidos")
	@JsonAlias("cantidad_vendidos")
	private Integer cantidadVendidos;
	
	@JsonProperty("stockTotal")
	@JsonAlias("stock_total")
	private Integer stockTotal;
	
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
}

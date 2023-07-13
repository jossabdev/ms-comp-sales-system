package io.jscode.microservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdmiProductoDTO {
	@JsonProperty("idProducto")
	@JsonAlias("id_producto")
	private Long idProducto;
	
	@JsonProperty("nombreProducto")
	@JsonAlias("nombre_producto")
	private String nombreProducto;
	
	@JsonProperty("descripcion")
	@JsonAlias("descripcion")
	private String descripcion;
	
	@JsonProperty("codigoBarras")
	@JsonAlias("codigo_barras")
	private String codigoBarras;
	
	@JsonProperty("categoria")
	@JsonAlias({"categoriaId", "categoria_id"})
	private AdmiCategoriaDTO categoria;
	
	@JsonProperty("costo")
	@JsonAlias("costo")
	private Double costo;
	
	@JsonProperty("precioVenta")
	@JsonAlias("precio_venta")
	private Double precioVenta;
	
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

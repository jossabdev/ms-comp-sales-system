package io.jscode.microservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdmiUsuarioDTO {
	
	@JsonProperty("idUsuario")
	@JsonAlias("idUsuario")
	private Long idUsuario;
	
	@JsonProperty("rol")
	@JsonAlias("rol")
	private AdmiRolDTO rol;
	
	@JsonProperty("usuario")
	@JsonAlias("usuario")
	private String usuario;
	
	@JsonProperty("email")
	@JsonAlias("email")
	private String email;
	
	@JsonProperty("password")
	@JsonAlias("password")
	private String password;
	
	@JsonProperty("locked")
	@JsonAlias("locked")
	private boolean locked;
	
	@JsonProperty("disabled")
	@JsonAlias("disabled")
	private boolean disabled;
	
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

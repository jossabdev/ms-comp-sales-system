package io.jscode.microservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		    .csrf(csrf -> csrf.disable())
		    .cors(Customizer.withDefaults())
		    .authorizeHttpRequests((httpRequest) -> httpRequest
		    	   .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("Administrador", "Vendedor")
		    	   .requestMatchers(HttpMethod.POST, "/ventas/**", "/detalleVentas/**", "/*/consultarPor").hasAnyRole("Administrador", "Vendedor")
		    	   .requestMatchers(HttpMethod.POST, "/inventarios/guardarInventario", "/categorias/guardarCategoria", "/productos/guardarProducto").hasRole("Administrador")
		    	   .requestMatchers(HttpMethod.PUT, "/inventarios/actualizarInventario", "/categorias/actualizarCategoria", "/productos/actualizarProducto", "/ventas/actualizarVenta", "/detalleVentas/actualizarDetalleVenta").hasRole("Administrador")
		    	   .requestMatchers(HttpMethod.DELETE, "/**").hasRole("Administrador")
		    	   .anyRequest()
		    	   .authenticated()		    	   
		    ).httpBasic(Customizer.withDefaults());
		
		return httpSecurity.build();
	}
	
	@Bean
	public UserDetailsService inMemoryUsers() {
		UserDetails admin = User.builder()
				.username("jbonzo")
				.password(passwordEncoder().encode("jbonzo"))				
				.roles("Administrador")
				.build();
		
		UserDetails vendedor = User.builder()
				.username("jespinoz")
				.password(passwordEncoder().encode("jespinoz"))				
				.roles("Vendedor")
				.build();
		
		return new InMemoryUserDetailsManager(admin, vendedor);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}

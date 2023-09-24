package io.jscode.microservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		    .csrf(csrf -> csrf.disable())
		    .cors(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		    .authorizeHttpRequests((httpRequest) -> httpRequest
				   .requestMatchers(HttpMethod.POST, "/auth/login/**").permitAll()
		    	   .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("Administrador", "Vendedor")
		    	   .requestMatchers(HttpMethod.POST, "/ventas/**", "/detalleVentas/**", "/*/consultarPor").hasAnyRole("Administrador", "Vendedor")
		    	   .requestMatchers(HttpMethod.POST, "/inventarios/guardarInventario", "/categorias/guardarCategoria", "/productos/guardarProducto").hasRole("Administrador")
		    	   .requestMatchers(HttpMethod.PUT, "/inventarios/actualizarInventario", "/categorias/actualizarCategoria", "/productos/actualizarProducto", "/ventas/actualizarVenta", "/detalleVentas/actualizarDetalleVenta").hasRole("Administrador")
		    	   .requestMatchers(HttpMethod.DELETE, "/**").hasRole("Administrador")
		    	   .anyRequest()
		    	   .authenticated()		    	   
		    )
			//.httpBasic(Customizer.withDefaults()) no se usara basic authentication sino jwt
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return httpSecurity.build();
	}
	
	/* Solo si necesitamos probar con usuarios en memoria
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
	}*/

	@Bean
	public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}

package io.jscode.microservice.datasource;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DbPostgresConfig {
	
	@Value("${spring.datasource.hikari.idle-timeout}")
	private int idleTimeout;
	
	@Value("${spring.datasource.hikari.connection-timeout}")
	private int connectionTimeout;
	
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private int maxPoolSize;
	
	@Value("${spring.datasource.hikari.minimum-idle}")
	private int minPoolSize;
	
	@Value("${spring.datasource.hikari.max-lifetime}")
	private int maxLifetime;
	
	
	private Map<String, Object> hibernateProperties(){
		Map<String, Object> hibernateProperties = new LinkedHashMap<>();
		hibernateProperties.put("hibernate.connection.release_mode", "auto");
		return hibernateProperties;
	}
	
	@Primary
	@Bean(name = "dsSalesSystem")
	public HikariDataSource dsSalesSystem(@Qualifier("dsSalesSystemProperties") HikariConfig dataSourceConfig) {
		return new HikariDataSource(dataSourceConfig);
	}
	
	@Primary
	@Bean(name = "dsSalesSystemProperties")
	@ConfigurationProperties("sales.system.datasource")
	public HikariConfig dsSalesSystemConfig() {
		HikariConfig dataSourceConfig = new HikariConfig();
		dataSourceConfig.setPoolName("dsSalesSystem");
		dataSourceConfig.setConnectionTimeout(connectionTimeout);
		dataSourceConfig.setIdleTimeout(idleTimeout);
		dataSourceConfig.setMaximumPoolSize(maxPoolSize);
		dataSourceConfig.setMinimumIdle(minPoolSize);
		dataSourceConfig.setMaxLifetime(maxLifetime);
		dataSourceConfig.setValidationTimeout(10000);
		return dataSourceConfig;
	}
	
	@Primary
	@Bean(name = "jdbcSalesSystem")
	@Autowired
	public JdbcTemplate jdbcSalesSystemTemplate(@Qualifier("dsSalesSystem") DataSource dsSalesSystem) {
		return new JdbcTemplate(dsSalesSystem);
	}
	
	@Primary
	@Bean(name = "SalesSystemEMFactory")
	public LocalContainerEntityManagerFactoryBean entitManagerFactorySisted (EntityManagerFactoryBuilder builder, @Qualifier("dsSalesSystem") DataSource dnSalesSystem) {
		return builder.dataSource(dnSalesSystem).properties(hibernateProperties()).packages("io.jscode.db.entity").persistenceUnit("dsSalesSystem").build();
	}
	
	@Primary
	@Bean(name = "SalesSystemTM")
	public PlatformTransactionManager  transactionManagerSalesSystem(@Qualifier("SalesSystemEMFactory") EntityManagerFactory salesSystemFactory) {
		return new JpaTransactionManager(salesSystemFactory);
	}
	
}

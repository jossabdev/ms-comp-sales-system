package io.jscode.microservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class ConsultaVentasDTO {
    private Double totalVentas;
    private Double totalGanancias;
    private String productoTop;
    private Double totalInversion;
    private List<DetalleVenta> detalleVentas;
    private List<InfoVentaCabDTO> ultimasVentasRealizadas;

    @Data
    public class DetalleVenta{
        private String mes;
        private Double totalVenta;
    }
}

package io.jscode.microservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class EstadisticaVentaDTO {
    private List<String> labels;
    private List<Double> values;
}

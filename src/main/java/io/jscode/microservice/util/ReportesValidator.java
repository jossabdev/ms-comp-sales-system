package io.jscode.microservice.util;

import java.util.List;

import org.springframework.stereotype.Component;

import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.util.ExcepcionGenerica;

@Component
public class ReportesValidator {
    
    public void validarListadoVentas(List<InfoVentaCabDTO> listaVentas) throws ExcepcionGenerica{
        if(listaVentas == null || listaVentas.isEmpty()){
            throw new ExcepcionGenerica("La lista de ventas es requerida");
        }
    }
}

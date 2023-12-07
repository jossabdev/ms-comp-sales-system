package io.jscode.microservice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.util.ReporteVentasExcel;
import io.jscode.microservice.util.ReportesValidator;
import io.jscode.util.ExcepcionGenerica;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "reportes")
public class ReportesController {

    @Autowired
    ReportesValidator reportesValidator;

    @PostMapping( path = "generarReporteVenta",  consumes = MediaType.APPLICATION_JSON_VALUE)
    public void generarReporteVenta(@RequestBody List<InfoVentaCabDTO> listaVentas, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
                
        try {
            reportesValidator.validarListadoVentas(listaVentas);

            ReporteVentasExcel generator = new ReporteVentasExcel(listaVentas);
            generator.generateExcelFile(response);
        } catch (ExcepcionGenerica e) {            
            e.printStackTrace();
        }                
    }
}

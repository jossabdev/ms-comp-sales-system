package io.jscode.microservice.util;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.jscode.microservice.dto.InfoVentaCabDTO;
import io.jscode.microservice.dto.InfoVentaDetDTO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ReporteVentasExcel {
    private List<InfoVentaCabDTO> listaVentas ;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ReporteVentasExcel( List<InfoVentaCabDTO> listaVentas){
        this.listaVentas = listaVentas;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader() {
        sheet = workbook.createSheet("ReporteVentas");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(row, 0, "Codigo Venta", style);
        createCell(row, 1, "Vendedor", style);
        createCell(row, 2, "Productos", style);
        createCell(row, 3, "Total", style);
        createCell(row, 4, "Fecha de venta", style);
        createCell(row, 5, "Estado", style);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        }else if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }

    private void write() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        CellStyle styleCurrency = workbook.createCellStyle();
        XSSFDataFormat df = workbook.createDataFormat();
        styleCurrency.setDataFormat(df.getFormat("$#,##0.00"));

        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        style.setFont(font);
        styleCurrency.setFont(font);

        Double totalVentas = 0.0;

        // escribe las ventas
        for (InfoVentaCabDTO venta: listaVentas) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            String productos = "";

            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);

            styleCurrency.setBorderBottom(BorderStyle.THIN);
            styleCurrency.setBorderLeft(BorderStyle.THIN);
            styleCurrency.setBorderRight(BorderStyle.THIN);
            styleCurrency.setBorderTop(BorderStyle.THIN);

            for(InfoVentaDetDTO detalleVenta: venta.getDetalleVenta()){
                productos = productos + detalleVenta.getProducto().getNombreProducto() + " ,";
            }

            // quita la ultima coma
            
            if(productos.length() > 0 && productos.contains(",")){
                productos = productos.substring(0, productos.length()-1);
            }

            createCell(row, columnCount++, venta.getIdVenta(), style);
            createCell(row, columnCount++, venta.getVendedor(), style);
            createCell(row, columnCount++, productos, style);
            createCell(row, columnCount++, venta.getTotal(), styleCurrency);
            createCell(row, columnCount++, venta.getFechaVenta().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")), style);
            createCell(row, columnCount++, venta.getEstado(), style);

            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            // saco total de ventas
            totalVentas = totalVentas + venta.getTotal();
        }

        rowCount++;

        // escribe el total de las ventas
        Row row = sheet.createRow(rowCount++);
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setFontHeight(12);
        style2.setFont(font2);

        style2.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);

        createCell(row, 0, "Total de ventas", style2);

        Row row2 = sheet.createRow(rowCount++);
        
        createCell(row2, 0, totalVentas, styleCurrency);
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}

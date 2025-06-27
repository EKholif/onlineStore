package com.onlineStore.admin.category.controller.utility;

import com.onlineStore.admin.abstarct.AbstractExporter;
import com.onlineStoreCom.entity.category.Category;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class CategoryExcelExporter extends AbstractExporter {


    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public CategoryExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeader() {
        sheet = workbook.createSheet("Categories");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 16);
        headerStyle.setFont(headerFont);

        String[] headers = {"ID  Name", "Alias", "Status"};
        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], headerStyle);
        }
    }


    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {

        XSSFCell cell = row.createCell(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }


    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {

        super.export(response, "application/octet-stream", ".xlsx", "Category_");

        writeHeader();

        writeDatelines(categoryList);


        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private void writeDatelines(List<Category> categoryList) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(14);
        cellStyle.setFont(font);


        for (Category category : categoryList) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            createCell(row, 0, category.getId(), cellStyle);
            createCell(row, 1, category.getName(), cellStyle);
            createCell(row, 2, category.getAlias(), cellStyle);
//            createCell(row ,3 ,category.getParent().toString() ,cellStyle );
//            createCell(row ,4 ,category.getChildren().toString(),cellStyle );
            createCell(row, 5, category.isEnable(), cellStyle);
        }
    }
}
package com.onlineStore.admin.pdfConvert.convertUtile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfToExcelConverter {

    public static void PdfToExcel(String pdfFilePath, String excelFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            XSSFWorkbook workbook = new XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sheet1");

            String[] lines = text.split("\\r?\\n");
            int rowNum = 0;
            for (String line : lines) {
                Row row = sheet.createRow(rowNum++);
                String[] cells = line.split("\t");
                int cellNum = 0;
                for (String cell : cells) {
                    Cell excelCell = row.createCell(cellNum++);
                    excelCell.setCellValue(cell);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }
        }
    }

    public static void main(String[] args) {
        String pdfFilePath = "/fileConvert/zz.pdf";
        String excelFilePath =  "/fileConvert/z33.xlsx";

        try {
            PdfToExcel(pdfFilePath, excelFilePath);
            System.out.println("PDF converted to Excel successfully.");
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

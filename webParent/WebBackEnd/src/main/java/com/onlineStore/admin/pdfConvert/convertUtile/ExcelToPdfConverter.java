package com.onlineStore.admin.pdfConvert.convertUtile;
import com.itextpdf.text.*;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

public class ExcelToPdfConverter {



    public static void ExcelToPdf(String excelFilePath, String pdfFilePath) throws IOException, DocumentException {
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        Document document = new Document();

        FileOutputStream outputStream = new FileOutputStream(pdfFilePath);

        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable table = new PdfPTable(getNumberOfColumns(workbook));
        addTableData(workbook, table);

        document.add(table);
        document.close();
        workbook.close();
    }

    private static int getNumberOfColumns(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        return row.getPhysicalNumberOfCells();
    }

    private static void addTableData(XSSFWorkbook workbook, PdfPTable table) throws DocumentException, IOException {
        XSSFSheet worksheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = worksheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }

            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                Cell cell = row.getCell(i);
                String cellValue = getCellValue(cell);
                PdfPCell cellPdf = new PdfPCell(new Phrase(cellValue, getCellStyle(cell)));
                setBackgroundColor(cell, cellPdf);
                setCellAlignment(cell, cellPdf);
                table.addCell(cellPdf);
            }
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String cellValue;
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                cellValue = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
                break;
            case BLANK:
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    private static Font getCellStyle(Cell cell) throws DocumentException, IOException {
        Font font = new Font();
        if (cell == null) {
            return font;
        }
        CellStyle cellStyle = cell.getCellStyle();
        org.apache.poi.ss.usermodel.Font cellFont = cell.getSheet().getWorkbook().getFontAt(cellStyle.getFontIndexAsInt());

        if (cellFont.getItalic()) {
            font.setStyle(Font.ITALIC);
        }
        if (cellFont.getStrikeout()) {
            font.setStyle(Font.STRIKETHRU);
        }
        if (cellFont.getUnderline() == 1) {
            font.setStyle(Font.UNDERLINE);
        }
        font.setSize(cellFont.getFontHeightInPoints());
        if (cellFont.getBold()) {
            font.setStyle(Font.BOLD);
        }

        String fontName = cellFont.getFontName();
        if (FontFactory.isRegistered(fontName)) {
            font.setFamily(fontName);
        } else {
            font.setFamily("Helvetica");
        }

        return font;
    }

    private static void setBackgroundColor(Cell cell, PdfPCell cellPdf) {
        if (cell == null) {
            return;
        }

        short bgColorIndex = cell.getCellStyle().getFillForegroundColor();
        if (bgColorIndex != IndexedColors.AUTOMATIC.getIndex()) {
            XSSFColor bgColor = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
            if (bgColor != null) {
                byte[] rgb = bgColor.getRGB();
                if (rgb != null && rgb.length == 3) {
                    cellPdf.setBackgroundColor(new BaseColor(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF));
                }
            }
        }
    }

    private static void setCellAlignment(Cell cell, PdfPCell cellPdf) {
        if (cell == null) {
            return;
        }
        CellStyle cellStyle = cell.getCellStyle();
        HorizontalAlignment horizontalAlignment = cellStyle.getAlignment();

        switch (horizontalAlignment) {
            case LEFT:
                cellPdf.setHorizontalAlignment(Element.ALIGN_LEFT);
                break;
            case CENTER:
                cellPdf.setHorizontalAlignment(Element.ALIGN_CENTER);
                break;
            case JUSTIFY:
            case FILL:
                cellPdf.setVerticalAlignment(Element.ALIGN_JUSTIFIED);
                break;
            case RIGHT:
                cellPdf.setHorizontalAlignment(Element.ALIGN_RIGHT);
                break;
        }
    }


    public static void main(String[] args) {
        String excelFilePath = "/fileConvert/z.xlsx";
        String pdfFilePath = "/fileConvert/zz.pdf";

        try {
            ExcelToPdf(excelFilePath, pdfFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}


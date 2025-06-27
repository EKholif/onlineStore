package com.onlineStore.admin.category.controller.utility;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onlineStore.admin.abstarct.AbstractExporter;
import com.onlineStoreCom.entity.category.Category;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static java.awt.Color.BLUE;
import static java.awt.Color.WHITE;

public class CategoryPdfCategoryExporter extends AbstractExporter {

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {
        super.export(response, "application/pdf", ".pdf", "Category_");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add header with logo
        addHeader(document);

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
        titleFont.setSize(24);
        Paragraph title = new Paragraph("List of Categories", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);
        Paragraph emptyLine = new Paragraph("\n\n"); // You can adjust the spacing as needed
        document.add(emptyLine);
        // Add table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1f, 2.5f, 2.5f, 1.5f});
        writeTableHeader(table);
        writeTableData(table, categoryList);
        document.add(table);

        document.close();
    }

    private void addHeader(Document document) {
        try {
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(10);

            // Add logo
            Image logo = Image.getInstance("categories-photos/rr.png");

            float percentage = 10f; // Adjust this value as needed
            float width = logo.getWidth() * percentage / 100;
            float height = logo.getHeight() * percentage / 100;

            logo.scaleToFit(width, height);
            PdfPCell logoCell = new PdfPCell(logo, true);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(logoCell);

            // Add empty space below the logo
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyCell);

            document.add(headerTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeTableData(PdfPTable table, List<Category> categoryList) {
        for (Category category : categoryList) {
            table.addCell(String.valueOf(category.getId()));
            table.addCell(category.getName());

            table.addCell(category.getAlias());
            table.addCell(category.isEnable() ? "Enabled" : "Disabled");
        }
    }

    private void writeTableHeader(PdfPTable table) {
        Font headerFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
        headerFont.setColor(WHITE);

        PdfPCell cell = new PdfPCell();


        cell.setBackgroundColor(BLUE);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell.setPhrase(new Phrase("ID", headerFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", headerFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Alias", headerFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Status", headerFont));
        table.addCell(cell);
    }
}
package com.onlineStore.admin.category.controller.utility;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onlineStoreCom.entity.prodact.Category;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CategoryPdfCategoryExporter extends AbstractCategoryExporter {

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {

       super.export(response,"application/pdf",".pdf");


        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,response.getOutputStream());
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ITALIC);
        font.setSize(18);
        font.setColor( Color.blue);
        Paragraph paragraph = new Paragraph("List of Categories", font);

        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] { 1f, 2.5f,2.5f,4f,3f,1.5f } );
        writerTableHeader(table);
        writerTableData(table, categoryList);

        document.add(table);

        document.close();
    }

    private void writerTableData(PdfPTable table, List<Category> categoryList) {

        for(Category category:categoryList){

            table.addCell(String.valueOf(category.getId()));
            table.addCell(String.valueOf(category.getName()));
            table.addCell(String.valueOf(category.getAlias()));
//            table.addCell(String.valueOf(category.getParent()));
//            table.addCell(String.valueOf(category.getChildren()));
            table.addCell(String.valueOf(category.isEnable()));
        }




    }

    private void writerTableHeader(PdfPTable table) {

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.yellow);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ITALIC);
        font.setSize(18);
        font.setColor( Color.black);

        cell.setPhrase(new Phrase(" ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase(" Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Alies", font));
        table.addCell(cell);

//        cell.setPhrase(new Phrase("Parent", font));
//        table.addCell(cell);
//
//        cell.setPhrase(new Phrase("Child", font));
//        table.addCell(cell);

        cell.setPhrase(new Phrase("status", font));
        table.addCell(cell);


    }
}

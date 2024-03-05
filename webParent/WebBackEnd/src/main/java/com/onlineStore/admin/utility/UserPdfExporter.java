package com.onlineStore.admin.utility;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onlineStore.admin.abstarct.AbstractExporter;
import com.onlineStoreCom.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

       super.export(response,"application/pdf",".pdf","users_");


        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,response.getOutputStream());
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ITALIC);
        font.setSize(18);
        font.setColor( Color.blue);
        Paragraph paragraph = new Paragraph("List of users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] { 1f, 2.5f,2.5f,4f,3f,1.5f } );
        writerTableHeader(table);
        writerTableData(table, listUsers);

        document.add(table);

        document.close();
    }

    private void writerTableData(PdfPTable table, List<User> listUsers) {

        for(User user:listUsers){

            table.addCell(String.valueOf(user.getId()));
            table.addCell(String.valueOf(user.getfirstName()));
            table.addCell(String.valueOf(user.getLastName()));
            table.addCell(String.valueOf(user.getEmail()));
            table.addCell(String.valueOf(user.getRoles()));
            table.addCell(String.valueOf(user.isEnable()));
        }




    }

    private void writerTableHeader(PdfPTable table) {

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.yellow);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ITALIC);
        font.setSize(18);
        font.setColor( Color.black);

        cell.setPhrase(new Phrase("User ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Roles", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("status", font));
        table.addCell(cell);


    }
}

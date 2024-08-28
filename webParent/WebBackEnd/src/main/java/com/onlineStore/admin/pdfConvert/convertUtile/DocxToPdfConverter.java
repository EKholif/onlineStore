package com.onlineStore.admin.pdfConvert.convertUtile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DocxToPdfConverter {

    public static void DocxToPdf(String docxFilePath, String pdfFilePath) {
        try (InputStream docxInputStream = new FileInputStream(docxFilePath);
             XWPFDocument document = new XWPFDocument(docxInputStream);
             OutputStream pdfOutputStream = new FileOutputStream(pdfFilePath)) {

            Document pdfDocument = new Document();
            PdfWriter.getInstance(pdfDocument, pdfOutputStream);
            pdfDocument.open();

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                pdfDocument.add(new Paragraph(paragraph.getText()));
            }

            pdfDocument.close();
            System.out.println("PDF created successfully!");

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputDocxPath = "/fileConvert/1.docx";
        String outputPdfPath = "/fileConvert/DocsOutput.pdf";


        DocxToPdf(inputDocxPath, outputPdfPath);
    }
}

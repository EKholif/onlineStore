package com.onlineStore.admin.pdfConvert.convertUtile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.*;

//todo tested ok

public class HtmlToPdfConverter {

    public static void HTMLToPDF(String htmlInputFilePath, String pdfOutFilePath) throws IOException, DocumentException {
        File htmlFile = new File(htmlInputFilePath);
        if (!htmlFile.exists() || !htmlFile.canRead()) {
            throw new IOException("HTML file does not exist or cannot be read: " + htmlInputFilePath);
        }

        File pdfFile = new File(pdfOutFilePath);
        File pdfDir = pdfFile.getParentFile();
        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        Document document = new Document();

        try (FileInputStream htmlInputStream = new FileInputStream(htmlInputFilePath);
             FileOutputStream pdfOutputStream = new FileOutputStream(pdfOutFilePath)) {

            PdfWriter writer = PdfWriter.getInstance(document, pdfOutputStream);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, htmlInputStream);
            document.close();

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;  // Rethrow the exception for further handling if needed
        }
    }
    public static void generatePDFFromHTMLo(String htmlContent, String outputPath) throws DocumentException, IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            System.out.println("FileOutputStream created.");
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            System.out.println("PdfWriter instance created.");
            document.open();
            System.out.println("Document opened.");
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(htmlContent));
            System.out.println("HTML content parsed.");
            document.close();
            System.out.println("Document closed.");
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;  // Rethrow the exception for further handling if needed
        }
    }

    public static void main(String[] args) {
        String dirNamePath = "/fileConvert/";
        try {
            HtmlToPdfConverter converter = new HtmlToPdfConverter();
            HTMLToPDF(dirNamePath + "a.html", dirNamePath + "aa.pdf");
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void generatePDFFromHTMLt(String htmlContent, String outputPath) throws DocumentException, IOException {
        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(htmlContent));
            document.close();
        }
    }

//    public static void main(String[] args) {
//        try {
//            String htmlContent = "<html><body><h1>Hello, Ehab >.. Good work!</h1></body></html>";
//            String dirNamePath = "/fileConvert/";
//            String idirnNamePath = "/fileConvert/";
//
//            String outputPath = dirNamePath+"\\outputTest.pdf";
//            generatePDFFromHTMLo(htmlContent, outputPath);
//        } catch (DocumentException | IOException e) {
//            e.printStackTrace();
//        }
//    }






}

package com.onlineStore.admin.pdfConvert.convertUtile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import java.io.*;
import java.nio.charset.StandardCharsets;


//Todo done tested
public class PdfToHtmlConverter {



    public void PdfToHtml(String pdfInputFilePath, String htmlOutFilePath) throws IOException {
        File pdfFile = new File(pdfInputFilePath);
        if (!pdfFile.exists() || !pdfFile.canRead()) {
            throw new IOException("PDF file does not exist or cannot be read: " + pdfInputFilePath);
        }

        File htmlFile = new File(htmlOutFilePath);
        File htmlDir = htmlFile.getParentFile();
        if (htmlDir != null && !htmlDir.exists()) {
            htmlDir.mkdirs();
        }

        try (PDDocument pdf = PDDocument.load(pdfFile);
             Writer output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(htmlOutFilePath), StandardCharsets.UTF_8))) {

            new PDFDomTree().writeText(pdf, output);
        } catch (IOException e) {
            throw new IOException("Error processing the PDF file: " + pdfInputFilePath, e);
        }
    }


    public static void main(String[] args) throws IOException {
        String dirNamePath = "/fileConvert/";
         PdfToHtmlConverter converter = new PdfToHtmlConverter();
        converter.PdfToHtml(dirNamePath +"input2.pdf" , dirNamePath + "a.html");
        System.out.println("PDF generated successfully.");
    }
}

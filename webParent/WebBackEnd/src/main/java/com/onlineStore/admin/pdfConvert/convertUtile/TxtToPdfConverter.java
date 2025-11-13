package com.onlineStore.admin.pdfConvert.convertUtile;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;



// todo test ok
public class TxtToPdfConverter {

    public static void TxtToPdf(String inputTxtFile, String outputPdfFile) throws IOException {
        File pdfFile = new File(outputPdfFile);
        File pdfDir = pdfFile.getParentFile();
        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        Document pdfDoc = new Document(PageSize.A4);
        try (BufferedReader br = new BufferedReader(new FileReader(inputTxtFile))) {
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(outputPdfFile));
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            pdfDoc.open();

            Font myFont = new Font();
            myFont.setStyle(Font.NORMAL);
            myFont.setSize(11);

            pdfDoc.add(new Paragraph("\n"));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                Paragraph para = new Paragraph(strLine + "\n", myFont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                pdfDoc.add(para);
            }
        } catch (DocumentException | IOException e) {
            throw new IOException("Error creating PDF from text file: " + inputTxtFile, e);
        } finally {
            if (pdfDoc.isOpen()) {
                pdfDoc.close();
            }
        }
    }

    public static void main(String[] args) {

        String dirNamePath = "/fileConvert/";

        try {
            TxtToPdfConverter converter = new TxtToPdfConverter();

            TxtToPdf(dirNamePath+"\\input.txt", dirNamePath+"\\output555.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

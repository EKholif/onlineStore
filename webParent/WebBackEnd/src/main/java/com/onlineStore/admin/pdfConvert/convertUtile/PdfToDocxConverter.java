package com.onlineStore.admin.pdfConvert.convertUtile;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfToDocxConverter {

    public static void PdfToDocx(String pdfFilename, String docxFilename) {
        PdfReader reader = null;
        XWPFDocument doc = null;
        FileOutputStream out = null;

        try {
            reader = new PdfReader(pdfFilename);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            doc = new XWPFDocument();
            out = new FileOutputStream(docxFilename);

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                String text = strategy.getResultantText();
                XWPFParagraph p = doc.createParagraph();
                XWPFRun run = p.createRun();
                run.setText(text);
                run.addBreak(BreakType.PAGE);
            }

            doc.write(out);
            System.out.println("DOCX created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            try {
                if (doc != null) {
                    doc.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        String docxPath = "/fileConvert/input99.docx";
        String pdfPath = "/fileConvert/input.pdf";


        PdfToDocx(pdfPath, docxPath);
    }
}

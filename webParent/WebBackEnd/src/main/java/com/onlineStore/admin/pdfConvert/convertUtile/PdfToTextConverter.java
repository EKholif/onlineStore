package com.onlineStore.admin.pdfConvert.convertUtile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

//todo Tested

public class PdfToTextConverter {

    public  static void PdfToTxT(String filename, String outputFilePath ) throws IOException {
        File file = new File(filename);
        if (!file.exists() || !file.canRead()) {
            throw new IOException("File does not exist or cannot be read: " + filename);
        }

        try (PDDocument pdDoc = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String parsedText = pdfStripper.getText(pdDoc);

            // Ensure the output directory exists
            File outputFile = new File(outputFilePath+filename+".txt");
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile))) {
                pw.print(parsedText);
            }
        } catch (IOException e) {
            throw new IOException("Error processing the PDF file: " + filename, e);
        }
    }

    public static void main(String[] args) {
        String dirNamePath = "/fileConvert/";
        try {

            PdfToTextConverter converter = new PdfToTextConverter();
            converter.PdfToTxT(dirNamePath+"\\input.pdf",dirNamePath+"\\output55.text");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.onlineStore.admin.pdfConvert.convertUtile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//todo done tested
public class PdfToImageConverter {

    public static void PDFtoImage(String pdfFilePath, String imageExtension) throws IOException {
        File pdfFile = new File(pdfFilePath);

        System.out.println("pdfFilePathpdfFilePath"+pdfFile.getAbsolutePath());
        if (!pdfFile.exists() || !pdfFile.canRead()) {

            System.out.println("File does not exist or cannot be read:ddddddddddd");
            throw new IOException("File does not exist or cannot be read: " + pdfFilePath);
        }

        File outputDir = new File(pdfFilePath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String outputFilename = imageExtension;

               
//                String outputFilename = String.format(pdfFilePath+"pdf-%d.%s", page + 1, imageExtension);
                System.out.println("outputFilenameoutputFilename"+outputFilename);
               ImageIOUtil.writeImage(bim, outputFilename, 300);
            }
        } catch (IOException e) {
            throw new IOException("Error processing the PDF file: " + pdfFilePath, e);
        }
    }

    public static void main(String[] args) {

        String dirNamePath = "/fileConvert";
        try {
            PdfToImageConverter converter = new PdfToImageConverter();
            converter.PDFtoImage(dirNamePath+"\\input2.pdf", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

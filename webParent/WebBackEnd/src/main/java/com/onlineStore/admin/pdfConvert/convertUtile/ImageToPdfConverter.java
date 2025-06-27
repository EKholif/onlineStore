package com.onlineStore.admin.pdfConvert.convertUtile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;



//todo tested ok

public class ImageToPdfConverter {

    public static void ImageToPdf(String input, String output) {
        Document document = new Document();


        try (FileOutputStream fos = new FileOutputStream(output)) {
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();
            Image image = Image.getInstance(input);
            document.add(image);
            document.close();
            writer.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        if (args.length != 2) {
//            System.err.println("Usage: java ImageToPdfConverter <filename> <extension>");
//            return;
//        }
//
//        String filename = args[0];
//        String extension = args[1];
//
//        try {
//            ImageToPdf(filename, extension);
//            System.out.println("PDF generated successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        String dirNamePath = "/fileConvert/";

        ImageToPdfConverter converter = new ImageToPdfConverter();
        ImageToPdf(dirNamePath + "1.jpg", dirNamePath + "22.pdf");
        System.out.println("PDF generated successfully.");
    }



}

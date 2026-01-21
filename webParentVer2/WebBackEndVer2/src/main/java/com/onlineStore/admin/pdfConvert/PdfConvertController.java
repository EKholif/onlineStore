package com.onlineStore.admin.pdfConvert;

import com.itextpdf.text.DocumentException;
import com.onlineStore.admin.pdfConvert.convertUtile.*;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.convertPdf.FileConvert;
import com.onlineStoreCom.entity.convertPdf.FileExtension;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController

public class PdfConvertController {
    @Autowired
    private FileConvertService service;

    @GetMapping("/convert/convert")
    public ModelAndView listAllCategories() {
        ModelAndView model = new ModelAndView("convert/convert");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        FileConvert fileConvert = new FileConvert();
        List<FileExtension> fileExtensions = Arrays.asList(FileExtension.values());

        model.addObject("fileExtensions", fileExtensions);
        model.addObject("pageTitle", "File Converter");

        model.addObject("fileExtensions", fileExtensions);
        model.addObject("pageTitle", "File Converter");
        model.addObject("fileConvert", fileConvert);
        model.addObject("saveChanges", "/convert/save-fileConvert");

        return model;

    }

    @PostMapping("/convert/save-fileConvert")
    public ModelAndView newConvertForm(@ModelAttribute FileConvert fileConvert, RedirectAttributes redirectAttributes,
                                       @RequestParam("fileUpload") MultipartFile multipartFile,
                                       @RequestParam("selectedExtension") String selectedExtension) throws IOException, DocumentException {

        redirectAttributes.addFlashAttribute("message", "the File   has been Converted successfully.  ");

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            fileConvert.setFilename(fileName);

            FileConvert savedInput = service.save(fileConvert);

            String uploadDir = "tenants/" + TenantContext.getTenantId() + "/assets/pdf-convert/" + savedInput.getId()
                    + "/";

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            String inputFileName = uploadDir + fileName;
            String outFileName = uploadDir + getFileName(fileName) + "." + selectedExtension;

            getPdfFile(inputFileName, outFileName);

            fromPDf(inputFileName, outFileName);

        }
        return new ModelAndView("redirect:/convert/convert");

    }

    public String getFileExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return ""; // No extension found
        }
    }

    public String getFileName(String fileName) {

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(0, dotIndex);
        } else {
            return fileName; // No extension found
        }
    }

    public String getPdfFile(String InputFilePath, String pdfOutFilePath) throws DocumentException, IOException {

        String fileUpload = getFileExtension(InputFilePath);

        switch (fileUpload) {

            case "pdf":
                return InputFilePath;

            case "html":
                HtmlToPdfConverter.HTMLToPDF(InputFilePath, pdfOutFilePath);
                break;

            case "xlsx":
                ExcelToPdfConverter.ExcelToPdf(InputFilePath, pdfOutFilePath);
                break;

            case "png":
            case "jpg":

                ImageToPdfConverter.ImageToPdf(InputFilePath, pdfOutFilePath);
                break;

            case "txt":
                TxtToPdfConverter.TxtToPdf(InputFilePath, pdfOutFilePath);

                break;

            case "docx":
                DocxToPdfConverter.DocxToPdf(InputFilePath, pdfOutFilePath);
                break;
        }

        return pdfOutFilePath;
    }

    public String fromPDf(String InputFilePath,

                          String pdfOutFilePath) throws DocumentException, IOException {

        String selectedExtension = getFileExtension(pdfOutFilePath);

        switch (selectedExtension) {
            case "pdf":
                return InputFilePath;

            case "html":
                HtmlToPdfConverter.HTMLToPDF(InputFilePath, pdfOutFilePath);
                break;

            case "xlsx":
                PdfToExcelConverter.PdfToExcel(InputFilePath, pdfOutFilePath);
                break;

            case "png":
            case "jpg":

                PdfToImageConverter.PDFtoImage(InputFilePath, pdfOutFilePath);
                break;

            case "txt":
                PdfToTextConverter.PdfToTxT(InputFilePath, pdfOutFilePath);

                break;

            case "docx":
                PdfToDocxConverter.PdfToDocx(InputFilePath, pdfOutFilePath);
                break;
        }

        return pdfOutFilePath;
    }

    // AG-CLEANUP: Removed 200+ lines of dead legacy code.
}

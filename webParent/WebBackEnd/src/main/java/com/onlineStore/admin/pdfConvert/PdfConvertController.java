package com.onlineStore.admin.pdfConvert;

import com.itextpdf.text.DocumentException;
import com.onlineStore.admin.pdfConvert.convertUtile.*;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.convertPdf.FileConvert;
import com.onlineStoreCom.entity.convertPdf.FileExtension;
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

            String dirName = "fileConvert/";
            String uploadDir = dirName + savedInput.getId() + "/";

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

    // ModelAndView model = new ModelAndView("convert/convert");
    //
    // FileConvert fileConvert = new FileConvert();
    //
    //
    // model.addObject("pageTitle", "File Converter");
    // model.addObject("label", "Parent Category :");
    // model.addObject("category", fileConvert);

    // return model;

    // }

    //
    //// todo : rundom id
    //
    //
    // @PostMapping("/categories/save-category")
    // public ModelAndView saveNewUCategory(@ModelAttribute Category category,
    // RedirectAttributes redirectAttributes
    // , @RequestParam("fileImage") MultipartFile multipartFile)
    // throws IOException {
    // redirectAttributes.addFlashAttribute("message", "the category has been saved
    // successfully. ");
    //
    //
    // if (!multipartFile.isEmpty()) {
    // String fileName =
    // StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    //
    // category.setImage(fileName);
    // Category savedCategory = service.saveCategory(category);
    //
    // String dirName = "categories-photos/";
    // String uploadDir = dirName + savedCategory.getId();
    //
    // FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    //
    // } else {
    //
    // service.saveCategory(category);
    // }
    //
    // return new ModelAndView("redirect:/categories/categories");
    //
    //
    // }
    //
    // @GetMapping("/categories/edit/{id}")
    // public ModelAndView editCategory(@PathVariable(name = "id") long id,
    // RedirectAttributes redirectAttributes) {
    // ModelAndView model = new ModelAndView("categories/new-categories-form");
    //
    //
    // try {
    //
    // Category existCategory = service.findById(id);
    // List<Category> listCategory = service.listUsedForForm();
    //
    // model.addObject("label", "Parent Category :");
    //
    // PagingAndSortingHelper pagingAndSortingHelper = new
    // PagingAndSortingHelper("categories", listCategory); // Corrected listName
    // return pagingAndSortingHelper.editForm(model, "category", existCategory, id);
    //
    //
    // } catch (CategoryNotFoundException ex) {
    // redirectAttributes.addFlashAttribute("message", ex.getMessage());
    // return new ModelAndView("redirect:/categories/categories");
    //
    //
    // }
    // }
    //
    // @PostMapping("/categories/save-edit-category/")
    // public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Long id,
    // @ModelAttribute Category category, RedirectAttributes redirectAttributes,
    // @RequestParam("fileImage") MultipartFile multipartFile) throws
    // CategoryNotFoundException, IOException {
    //
    // redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + "
    // has been updated successfully. ");
    //
    // Category updateCategory = service.findById(id);
    //
    //
    // if (multipartFile.isEmpty()) {
    // BeanUtils.copyProperties(category, updateCategory, "id", "image");
    // service.saveCategory(updateCategory);
    //
    // } else if (!multipartFile.isEmpty()) {
    //
    // FileUploadUtil.cleanDir(updateCategory.getImageDir());
    // String fileName =
    // StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    // String uploadDir = "categories-photos/" + updateCategory.getId();
    // category.setImage(fileName);
    // BeanUtils.copyProperties(category, updateCategory, "id");
    //
    // service.saveCategory(updateCategory);
    // FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    //
    // }
    //
    // return new ModelAndView("redirect:/categories/categories");
    // }
    //
    //
    // @GetMapping("/delete-category/{id}")
    // public ModelAndView deleteCategory(@PathVariable(name = "id") Long id,
    // RedirectAttributes redirectAttributes) throws CategoryNotFoundException,
    // IOException {
    //
    // try {
    // if (service.existsById(id)) {
    // FileUploadUtil.cleanDir(service.findById(id).getImageDir());
    //
    // service.deleteCategory(id);
    // redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + "
    // has been Deleted");
    // } else {
    // redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + "
    // Category Not Found");
    //
    // }
    //
    // return new ModelAndView("redirect:/categories/categories");
    // } catch (CategoryNotFoundException | IOException ex) {
    // redirectAttributes.addFlashAttribute("message", " Category Not Found");
    // }
    // return new ModelAndView("redirect:/categories/categories");
    // }
    //
    //
    // @GetMapping("/category/{id}/enable/{status}")
    // public ModelAndView UpdateUserStatus(@PathVariable("id") Long id,
    // @PathVariable("status") boolean enable,
    // RedirectAttributes redirectAttributes) {
    // service.UpdateCategoryEnableStatus(id, enable);
    // String status = enable ? "enable" : " disable";
    // String message = " the user Id : " + id + " has bean " + status;
    // redirectAttributes.addFlashAttribute("message", message);
    //
    // return new ModelAndView("redirect:/categories/categories");
    //
    // }
    //
    // @PostMapping("/categories/deleteCategories")
    // public ModelAndView deleteCategory(@RequestParam(name = "selectedCategory",
    // required = false) List<Long> selectedCategory,
    // RedirectAttributes redirectAttributes) throws CategoryNotFoundException,
    // IOException {
    //
    // redirectAttributes.addFlashAttribute("message", "the Category ID: " +
    // selectedCategory + " has been Deleted");
    // ModelAndView model = new ModelAndView("categories/categories");
    //
    // model.addObject("label", selectedCategory);
    //
    // if (selectedCategory != null && !selectedCategory.isEmpty()) {
    // for (Long id : selectedCategory) {
    // FileUploadUtil.cleanDir(service.findById(id).getImageDir());
    // service.deleteCategory(id);
    // }
    // }
    // return new ModelAndView("redirect:/categories/categories");
    // }
    //
    //
    // @GetMapping("/categories/export/csv")
    // public void exportToCsv(HttpServletResponse response) throws IOException {
    // List<Category> listCategories = service.listUsedForForm();
    // CategoryCsvCategoryExporter userCsvExporter = new
    // CategoryCsvCategoryExporter();
    // userCsvExporter.export(listCategories, response);
    //
    // }
    //
    // @GetMapping("/categories/export/excel")
    // public void exportToExcel(HttpServletResponse response) throws IOException {
    // List<Category> categoryList = service.listUsedForForm();
    //
    // CategoryExcelExporter categoryExcelExporter = new CategoryExcelExporter();
    // categoryExcelExporter.export(categoryList, response);
    //
    //
    // }
    //
    // @GetMapping("/categories/export/pdf")
    // public void exportToPdf(HttpServletResponse response) throws IOException {
    // List<Category> categoryList = service.listUsedForForm();
    //
    // CategoryPdfCategoryExporter categoryPdfCategoryExporter = new
    // CategoryPdfCategoryExporter();
    // categoryPdfCategoryExporter.export(categoryList, response);
    //
    // }
    //

}

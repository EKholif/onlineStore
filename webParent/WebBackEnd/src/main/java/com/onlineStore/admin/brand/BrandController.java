package com.onlineStore.admin.brand;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class BrandController {
    @Autowired
    private BrandService service ;


    @GetMapping("/brand/brands")
    public ModelAndView listAllbrands( ) {
        ModelAndView model = new ModelAndView("/brand/brands");

        model.addObject("pageTitle","List brands" );

        model.addObject("brands", service.listAll());
//        model.addObject("modelUrl", "/brand/brands");

        return model;
//        return listByPage(1,"name","asc",null);a
    }


//    @GetMapping("/brands/page/{pageNum}")
//    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
//                                   @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
//                                   @Param("keyWord") String keyWord) {
//
//        ModelAndView model = new ModelAndView("/brand/brands");
//
//
//        Page<User> userPage = service.listByPage(pageNum, sortFiled, sortDir, keyWord);
//        List<User> listUsers= userPage.getContent();
//
//        long startCount = (long) (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
//        long endCount = startCount+ UserService.USERS_PER_PAGE -1;
//
//
//        if(endCount> userPage.getTotalElements()){
//            endCount=userPage.getTotalElements();
//        }
//        String reverseSortDir= sortDir.equals("asc")?"dsc":"asc";
//
//        model.addObject("totalItems", userPage.getTotalElements());
//        model.addObject("totalPages", userPage.getTotalPages());
//        model.addObject("sortFiled" , sortFiled);
//        model.addObject("sortDir" , sortDir);
//        model.addObject("currentPage", pageNum);
//        model.addObject("endCount", endCount);
//        model.addObject("startCont", startCount);
//        model.addObject("users", listUsers);
//        model.addObject("reverseSortDir", reverseSortDir);
//        model.addObject("keyWord", keyWord);
//        model.addObject("search" , "/users/page/1");
//        model.addObject("modelUrl", "/users/page/");
//        return  model;
//    }

//}
//
//    public static <T> List<T> getPage(List<T> list, int pageNumber, int pageSize) {
//        int startIndex = (pageNumber - 1) * pageSize;
//        int endIndex = Math.min(startIndex + pageSize, list.size());
//        return list.subList(startIndex, endIndex);
//    }
//
//
//    @GetMapping("/brands/new-categories-form")
//    public ModelAndView newCategoryForm() {
//
//        ModelAndView model = new ModelAndView("/brands/new-categories-form");
//
//        Category newCategory = new Category();
//
//        newCategory.setEnable(true);
//
//        List<Category> listCategory = service.listUsedForForm();
//
//
//        model.addObject("category", newCategory);
//        model.addObject("label", "Parent Category :");
//
//        model.addObject("listCategory",listCategory );
//        model.addObject("pageTitle","Creat new Category" );
//        model.addObject("saveChanges", "/brands/save-category");
//        model.addObject("id", 0L);
//        return model;
//
//    }
//
//
////    todo : rundom id
//
//
//    @PostMapping("/brands/save-category")
//    public ModelAndView saveNewUCategory(@ModelAttribute  Category category,
//                                         RedirectAttributes redirectAttributes
//            , @RequestParam("fileImage") MultipartFile multipartFile)
//            throws IOException {
//        redirectAttributes.addFlashAttribute("message", "the category   has been saved successfully.  ");
//
//
//        if (!multipartFile.isEmpty()) {
//            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//
//            System.out.println(fileName);
//
//            category.setImage(fileName);
//            Category savedCategory = service.saveCategory(category);
//
//            String dirName = "brands-photos/";
//            String uploadDir = dirName + savedCategory.getId();
//
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//
//        } else {
//
//            service.saveCategory(category);
//        }
//
//        return new ModelAndView("redirect:/brand/brands");
//
//
//    }
//
//    @GetMapping("/category/edit/{id}")
//    public ModelAndView editCategory(@PathVariable (name="id")long id, RedirectAttributes redirectAttributes) {
//
//
//        try {
//            ModelAndView model = new ModelAndView("/brands/new-categories-form");
//
//            Category ExistCategory = service.getCategory(id);
//
//            List<Category> listCategory = service.listUsedForForm();
//
//            model.addObject("category", ExistCategory);
//
//            model.addObject("label", "Parent Category :");
//
//            model.addObject("listCategory", listCategory);
//            model.addObject("saveChanges", "/brands/save-edit-category/");
//
//            model.addObject("pageTitle", "Edit " + ExistCategory.getName() + " (ID: " + id + ")");
//            model.addObject("id", id);
//
//
//            return model;
//
//        } catch (CategoryNotFoundException ex) {
//            redirectAttributes.addFlashAttribute("message", ex.getMessage());
//            return new ModelAndView("redirect:/brand/brands");
//
//
//        }
//    }
//
//    @PostMapping( "/brands/save-edit-category/")
//    public ModelAndView saveUpdaterUser(@RequestParam ( name="id") Long id,@ModelAttribute  Category category, RedirectAttributes redirectAttributes,
//                                       @RequestParam("fileImage") MultipartFile multipartFile ) throws CategoryNotFoundException, IOException {
//
//        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id+  " has been updated successfully. ");
//
//        Category updateCategory =service.getCategory(id);
//
//
//
//            if (multipartFile.isEmpty()) {
//                BeanUtils.copyProperties(  category,updateCategory,"id",  "image");
//                service.saveCategory(updateCategory);
//
//               } else if (!multipartFile.isEmpty()) {
//
//                FileUploadUtil.cleanDir(updateCategory.getImageDir());
//                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//                String uploadDir = "brands-photos/" + updateCategory.getId();
//                category.setImage(fileName);
//                BeanUtils.copyProperties( category,updateCategory,"id"  );
//
//                service.saveCategory(updateCategory);
//                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//
//            }
//
//        return new ModelAndView("redirect:/brand/brands");
//    }
//
//
//
//    @GetMapping ("/brands/delete-Category/{id}")
//        public ModelAndView deleteCategory(@PathVariable (name = "id")Long id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {
//
//        try {
//            if (categoryRepository.existsById(id)) {
//                FileUploadUtil.cleanDir( service.getCategory(id).getImageDir());
//
//           service.deleteCategory(id);
//                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " has been Deleted");
//            } else {
//                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " Category Not Found");
//
//            }
//
//            return new ModelAndView("redirect:/brand/brands");
//        }
//
//        catch (CategoryNotFoundException | IOException ex) {
//            redirectAttributes.addFlashAttribute("message", " Category Not Found");
//        }
//            return new ModelAndView("redirect:/brand/brands");
//        }
//
//
//    @GetMapping("/category/{id}/enable/{status}")
//    public ModelAndView UpdateUserStatus (@PathVariable("id")Long id, @PathVariable("status") boolean enable,
//                                    RedirectAttributes redirectAttributes){
//        service.UpdateCategoryEnableStatus(id,enable);
//        String status = enable ? "enable" :" disable";
//        String message = " the user Id :   " + id +" has bean  " +status ;
//        redirectAttributes.addFlashAttribute("message", message);
//
//        return new ModelAndView("redirect:/brand/brands");
//
//       }
//
//    @PostMapping("/brands/deletebrands")
//    public ModelAndView deleteCategory(@RequestParam(name = "selectedCategory", required = false) List<Long> selectedCategory,
//                              RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {
//
//        redirectAttributes.addFlashAttribute("message", "the Category ID: " + selectedCategory + " has been Deleted");
//        ModelAndView model = new ModelAndView("/brand/brands");
//
//        model.addObject("label", selectedCategory);
//
//        if (selectedCategory != null && !selectedCategory.isEmpty()) {
//            for (Long userId : selectedCategory) {
//                FileUploadUtil.cleanDir( service.getCategory(userId).getImageDir());
//                service.deleteCategory(userId);
//            }
//        }
//        return new ModelAndView("redirect:/brand/brands");
//    }
//
//
//@GetMapping("/brands/export/csv")
//    public void exportToCsv(HttpServletResponse response) throws IOException {
//        List<Category> listbrands = service.listUsedForForm();
//        CategoryCsvCategoryExporter userCsvExporter = new CategoryCsvCategoryExporter();
//        userCsvExporter.export(listbrands,response);
//
//}
//    @GetMapping("/brands/export/excel")
//    public void exportToExcel(HttpServletResponse response) throws IOException {
//        List<Category> categoryList = service.listUsedForForm();
//
//        CategoryExcelExporter categoryExcelExporter = new  CategoryExcelExporter();
//        categoryExcelExporter.export(categoryList,response);
//
//
//    }
//    @GetMapping("/brands/export/pdf")
//    public void exportToPdf(HttpServletResponse response) throws IOException {
//        List<Category> categoryList = service.listUsedForForm();
//
//        CategoryPdfCategoryExporter categoryPdfCategoryExporter = new  CategoryPdfCategoryExporter();
//        categoryPdfCategoryExporter.export(categoryList,response);
//
//    }


    }




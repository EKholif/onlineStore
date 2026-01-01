package com.onlineStore.admin.usersAndCustomers.users.controller;

import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.category.controller.PagingAndSortingHelper;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.security.tenant.TenantService;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.UserCsvExporter;
import com.onlineStore.admin.utility.UserExcelExporter;
import com.onlineStore.admin.utility.UserPdfExporter;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/users/users")
    public ModelAndView listAllUsers() {
        ModelAndView model = new ModelAndView("users/users");

        Long tenantId = TenantContext.getTenantId();
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Filter filter = session.getEnabledFilter("tenantFilter");

        return listByPage(1, "firstName", "dsc", null);
    }

    @GetMapping("/users/page/{pageNum}")
    public ModelAndView listByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                   @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord) {

        ModelAndView model = new ModelAndView("users/users");
        PageInfo pageInfo = new PageInfo();

        List<User> listByPage = service.listByPage(pageInfo, pageNum, sortField, sortDir, keyWord);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper(model, "users", sortField, sortDir,
                keyWord, pageNum, listByPage);

        pagingAndSortingHelper.listByPage(pageInfo, "users");

        return model;
    }

    @GetMapping("/register/new-users-form")

    public ModelAndView newUser() {

        ModelAndView model = new ModelAndView("register/new-users-form");

        User user = new User();
        List<Role> listAllRoles = service.listAllRoles();

        user.setEnable(true);
        model.addObject("id", 0);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("users", listAllRoles); // Corrected
        // listName
        return pagingAndSortingHelper.newForm(model, "user", user);

    }

    @GetMapping("/users/new-users-form")
    public ModelAndView newUserForm() {
        ModelAndView model = new ModelAndView("users/new-users-form");

        User user = new User();
        List<Role> listAllRoles = service.listAllRoles();

        user.setEnable(true);
        model.addObject("id", 0);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("users", listAllRoles); // Corrected
        // listName
        return pagingAndSortingHelper.newForm(model, "user", user);

    }

    @PostMapping("/users/save-user")
    public ModelAndView saveNewUser(@ModelAttribute User user,
                                    RedirectAttributes redirectAttributes,
                                    @RequestParam("image") MultipartFile multipartFile) throws UsernameNotFoundException, IOException {
        redirectAttributes.addFlashAttribute("message", "the user   has been saved successfully.  ");
        String dirName = "user-photos/";

        Long tenantId = TenantContext.getTenantId();

        // [AG-TEN-REQ-001] Explicitly assign Tenant ID to new users
        if (user.getTenantId() == null || user.getTenantId() == 0) {
            // Business Value: Self-registration flow generates a new tenant scope.
            user.setTenantId(TenantService.createTenant());
        } else {
            // Business Value: Admin-created users inherit the admin's tenant scope.
            user.setTenantId(tenantId);
        }

        if (!multipartFile.isEmpty()) {
            savePhoto(user, multipartFile, dirName);
        }

        user.setTenantId(getTenantId(user));

        service.saveUser(user);
        return new ModelAndView("redirect:/users/users");
    }

    public <T extends IdBasedEntity> Long getTenantId(T entity) {
        return entity.getTenantId();
    }

    private void savePhoto(User user, MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        user.setUser_bio(fileName);

        User savedUser = service.saveUser(user);

        String uploadDir = dirName + savedUser.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    @GetMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {

        ModelAndView model = new ModelAndView("users/new-users-form");
        try {
            User user = service.getUser(id);
            List<Role> listAllRoles = service.listAllRoles();

            model.addObject("listItems", user.getRoles());

            PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("users", listAllRoles); // Corrected
            // listName
            return pagingAndSortingHelper.editForm(model, "user", user, id);

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/users/users");

        }
    }

    @PostMapping("/users/save-edit-user")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Integer id, @ModelAttribute User user,
                                        RedirectAttributes redirectAttributes,
                                        @RequestParam("image") MultipartFile multipartFile) throws UsernameNotFoundException, IOException {
        redirectAttributes.addFlashAttribute("message", "the user Id : " + id + " has been updated successfully. ");

        User updateUser = service.getUser(id);

        if (user.getPassword().isEmpty()) {

            if (multipartFile.isEmpty()) {
                BeanUtils.copyProperties(user, updateUser, "id", "user_bio", "password");
                service.saveUpdatededUser(updateUser);

            } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateUser.getImageDir());
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "user-photos/" + updateUser.getId();
                user.setUser_bio(fileName);
                BeanUtils.copyProperties(user, updateUser, "id", "password");
                service.saveUpdatededUser(updateUser);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }

        } else {

            if (multipartFile.isEmpty()) {

                BeanUtils.copyProperties(user, updateUser, "id", "user_bio");

                service.saveUser(updateUser);

            } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateUser.getImageDir());

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "user-photos/" + updateUser.getId();
                user.setUser_bio(fileName);
                BeanUtils.copyProperties(user, updateUser, "id");
                service.saveUser(updateUser);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }

        }
        String fristPartEmail = user.getEmail().split("@")[0];
        return new ModelAndView("redirect:/users/page/1?sortField=id&sortDir=asc&keyWord=" + fristPartEmail);
    }

    @GetMapping("/delete-user/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (service.existsById(id)) {
                FileUploadUtil.cleanDir(service.getUser(id).getImageDir());
                service.deleteUser(id);
                redirectAttributes.addFlashAttribute("message",
                        "User with ID " + id + " has been successfully deleted.");
            } else {
                redirectAttributes.addFlashAttribute("message", "User with ID " + id + " not found.");
            }
        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "User with ID " + id + " not found.");
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("message", "Error occurred while deleting user with ID " + id + ".");
            // Log the exception for further investigation
            ex.printStackTrace();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred.");
            // Log the exception for further investigation
            ex.printStackTrace();
        }
        return new ModelAndView("redirect:/users/users");
    }

    @GetMapping("/user/{id}/enable/{status}")
    public ModelAndView UpdateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enable,
                                         RedirectAttributes redirectAttributes) {
        service.UdpateUserEnableStatus(id, enable);
        String status = enable ? "enable" : " disable";
        String message = " the user Id  " + id + "  has bean  " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return new ModelAndView("redirect:/users/users");

    }

    @PostMapping("/deleteUsers")
    public ModelAndView deleteUsers(@RequestParam(name = "selectedUsers", required = false) List<Integer> selectedUsers,
                                    RedirectAttributes redirectAttributes) throws UsernameNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Users ID: " + selectedUsers + " has been Deleted");

        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            for (Integer id : selectedUsers) {
                FileUploadUtil.cleanDir(service.getUser(id).getImageDir());
                service.deleteUser(id);
            }
        }

        return new ModelAndView("redirect:/users/users");
    }

    @GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAllUsers();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(listUsers, response);

    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAllUsers();
        UserExcelExporter userExcelExporter = new UserExcelExporter();
        userExcelExporter.export(listUsers, response);

    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAllUsers();
        UserPdfExporter UserPdfExporter = new UserPdfExporter();
        UserPdfExporter.export(listUsers, response);

    }

}

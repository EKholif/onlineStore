package com.onlineStore.admin.usersAndCustomers.users.controller;

import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.security.StoreBackendUserDetails;
import com.onlineStore.admin.security.tenant.TenantService;
import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import com.onlineStore.admin.utility.UserCsvExporter;
import com.onlineStore.admin.utility.UserExcelExporter;
import com.onlineStore.admin.utility.UserPdfExporter;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
// AG-CLEANUP: Removed FileUploadUtil usage

@org.springframework.stereotype.Controller
public class UserController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;
    @Autowired
    private EntityManager entityManager;

    @GetMapping("/users/users")
    public String listAllUsers() {
        return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "users", moduleURL = "/users/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        Page<com.onlineStoreCom.entity.users.User> page = service.listByPage(pageNum, helper.getSortField(),
                helper.getSortDir(), helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);

        return "users/users";
    }

    @GetMapping("/register/new-users-form")
    public ModelAndView newUser() {
        ModelAndView model = new ModelAndView("register/new-users-form");

        com.onlineStoreCom.entity.users.User user = new com.onlineStoreCom.entity.users.User();
        List<com.onlineStoreCom.entity.users.Role> listAllRoles = service.listAllRoles();

        user.setEnabled(true);
        model.addObject("id", 0);
        model.addObject("user", user);
        model.addObject("listItems", listAllRoles);
        model.addObject("pageTitle", "Create new user");
        model.addObject("saveChanges", "/users/save-user");

        return model;
    }

    @GetMapping("/users/new-users-form")
    public ModelAndView newUserForm() {
        ModelAndView model = new ModelAndView("users/new-users-form");

        com.onlineStoreCom.entity.users.User user = new com.onlineStoreCom.entity.users.User();
        List<com.onlineStoreCom.entity.users.Role> listAllRoles = service.listAllRoles();

        user.setEnabled(true);
        model.addObject("id", 0);
        model.addObject("user", user);
        model.addObject("listItems", listAllRoles);
        model.addObject("pageTitle", "Create new user");
        model.addObject("saveChanges", "/users/save-user");

        return model;
    }

    @PostMapping("/users/save-user")
    public ModelAndView saveNewUser(@ModelAttribute com.onlineStoreCom.entity.users.User user,
            RedirectAttributes redirectAttributes,
            @RequestParam("image") MultipartFile multipartFile) throws UsernameNotFoundException, IOException {

        Long tenantId = TenantContext.getTenantId();

        // [AG-TEN-REQ-001] Explicitly assign Tenant ID to new users
        if (user.getTenantId() == null || user.getTenantId() == 0) {
            // Business Value: Self-registration flow generates a new tenant scope.
            user.setTenantId(TenantService.createTenant());
        } else {
            // Business Value: Admin-created users inherit the admin's tenant scope.
            user.setTenantId(tenantId);
        }

        // AG-REFACTOR: Delegate to Service
        service.saveUser(user, multipartFile);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return new ModelAndView("redirect:/users/users");
    }

    public <T extends IdBasedEntity> Long getTenantId(T entity) {
        return entity.getTenantId();
    }

    // AG-CLEANUP: Removed savePhoto (logic moved to Service)

    @GetMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {

        ModelAndView model = new ModelAndView("users/new-users-form");
        try {
            com.onlineStoreCom.entity.users.User user = service.getUser(id);
            List<com.onlineStoreCom.entity.users.Role> listAllRoles = service.listAllRoles();

            model.addObject("listItems", user.getRoles()); // Wait, this logic seems wrong in original code, passing
            // user roles as listItems?
            // Original code: model.addObject("listItems", user.getRoles());
            // Then helper: model.addObject("listItems", listItems); (which was passed as
            // listAllRoles to constructor)
            // It seems the original code was overwriting "listItems" or confusing it.
            // "listItems" in form usually means "Available Roles".
            // I will assume listAllRoles is what's needed for the select box.

            model.addObject("user", user);
            model.addObject("id", id);
            model.addObject("listItems", listAllRoles);
            model.addObject("pageTitle", " Edit : user ID :  " + id);
            model.addObject("saveChanges", "/users/save-edit-user"); // Note: removed trailing slash based on pattern

            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/users/users");

        }
    }

    @PostMapping("/users/save-edit-user")
    public ModelAndView saveUpdatedUser(@RequestParam(name = "id") Integer id,
                                        @ModelAttribute com.onlineStoreCom.entity.users.User user,
            RedirectAttributes redirectAttributes,
            @RequestParam("image") MultipartFile multipartFile) throws UsernameNotFoundException, IOException {
        try {
            com.onlineStoreCom.entity.users.User existingUser = service.getUser(id);

            // Copy mutable fields from form (user) to existingUser
            // Exclude ID, Password, Photos, Tenants (handled separately or preserved)
            BeanUtils.copyProperties(user, existingUser, "id", "password", "photos", "tenants", "createdTime",
                    "verificationCode");

            // Handle Password: If form has value, set it. If empty, keep existing (Service
            // will handle if we passed DTO, but here we pass Entity)
            // But wait, Service `saveUser` logic:
            /*
             * if (user.getPassword().isEmpty()) { ... } else { encodePassword(user); }
             */
            // If we update `existingUser.password` with RAW usage, it isn't empty. Service
            // will encode it. Correct.
            // If we DON'T update `existingUser.password`, it is HASH. Service will encode
            // HASH. Incorrect.

            // FIX: We must check here.
            if (!user.getPassword().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            } else {
                // If empty, we want to keep existing hash.
                // But Service will re-encode it because it's not empty?
                // Service logic needs to know if it's raw or hash.
                // Since we can't easily know, we should rely on the Service Logic I wrote:
                // "if (user.getPassword().isEmpty())" -> copies from DB.

                // SO: To use Service logic, we must pass an object with EMPTY password if we
                // want to keep old one.
                // existingUser has HASH.

                // TRICK: Set existingUser.password to "" if form was empty?
                // No, then Service copies from... itself? (load from DB).

                // Service: "User existingUser = userRepo.findById(user.getId())"
                // So if we pass 'existingUser' (arg) with password "", Service loads DB version
                // (hash), sets arg.password = hash.
                // Perfect.
                existingUser.setPassword("");
            }

            existingUser.setRoles(user.getRoles());
            // Tenants usually not editable in basic form, but if so:
            // existingUser.setTenants(user.getTenants());

            service.saveUser(existingUser, multipartFile);

            // Refresh Security Context
            StoreBackendUserDetails userDetails = new StoreBackendUserDetails(service.getUser(existingUser.getId()));
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error updating user: " + e.getMessage());
        }
        return new ModelAndView("redirect:/users/page/1?sortField=id&sortDir=asc");
    }

    @GetMapping("/delete-user/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // AG-REFACTOR: Service handles cleanup
            service.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User with ID " + id + " has been successfully deleted.");
        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "User with ID " + id + " not found.");
        } catch (Exception ex) {
            LOGGER.error("Error deleting user: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred.");
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
                                    RedirectAttributes redirectAttributes) {

        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            for (Integer id : selectedUsers) {
                try {
                    service.deleteUser(id);
                } catch (UsernameNotFoundException e) {
                    LOGGER.warn("Attempted to delete non-existent user ID: {}", id);
                }
            }
            redirectAttributes.addFlashAttribute("message", "Selected users have been deleted.");
        } else {
            redirectAttributes.addFlashAttribute("message", "No users selected.");
        }

        return new ModelAndView("redirect:/users/users");
    }

    @GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<com.onlineStoreCom.entity.users.User> listUsers = service.listAllUsers();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(listUsers, response);

    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<com.onlineStoreCom.entity.users.User> listUsers = service.listAllUsers();
        UserExcelExporter userExcelExporter = new UserExcelExporter();
        userExcelExporter.export(listUsers, response);

    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<com.onlineStoreCom.entity.users.User> listUsers = service.listAllUsers();
        UserPdfExporter UserPdfExporter = new UserPdfExporter();
        UserPdfExporter.export(listUsers, response);

    }

}

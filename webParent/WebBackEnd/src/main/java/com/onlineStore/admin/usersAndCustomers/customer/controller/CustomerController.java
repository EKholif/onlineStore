package com.onlineStore.admin.usersAndCustomers.customer.controller;

import com.onlineStore.admin.setting.service.SettingService;
import com.onlineStore.admin.usersAndCustomers.customer.service.CustomerService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import org.springframework.data.domain.Page;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService service;
    @Autowired
    private SettingService settingService;

    private final String defaultRedirectURL = "redirect:/customer/customer";

    @GetMapping("/customer/customer")
    public String listAllCustomers() {

        return "redirect:/customer/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/customer/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "customer", moduleURL = "/customer/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        Page<Customer> page = service.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);

        return "customer/customer";
    }

    @GetMapping("/customer/new-customer-form")
    public ModelAndView showRegistrationForm() {
        ModelAndView model = new ModelAndView("customer/new-customer-form");

        Customer customer = new Customer();
        String pageTitle = "Customer Registration";
        List<Country> countriesList = service.listAllCountries();

        model.addObject("customer", customer);
        model.addObject("listItems", countriesList); // Assuming template uses listItems
        model.addObject("pageTitle", pageTitle);
        model.addObject("saveChanges", "/customer/save-customer");

        return model;
    }

    @PostMapping("/customer/save-customer")
    public String saveNewCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes,
                                  HttpServletRequest request,
                                  @RequestParam("fileImage") MultipartFile multipartFile) throws IOException, MessagingException {
        redirectAttributes.addFlashAttribute("message", "the Customer has been saved successfully.  ");
        boolean newCustomer = customer.getId() == null;
        Customer savedCustomer;
        Long tenantId = TenantContext.getTenantId();
        customer.setTenantId(tenantId);
        if (!service.isEmailUnique(customer.getId(), customer.getEmail())) {
            redirectAttributes.addFlashAttribute("message",
                    "The email " + customer.getEmail() + " is already registered.");
            return "redirect:/customer/customer";
        }

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            customer.setImage(fileName);
            savedCustomer = service.saveCustomer(customer);

            String dirName = "customers-photos/";
            savePhoto(savedCustomer, multipartFile, dirName);

        } else {

            savedCustomer = service.saveCustomer(customer);
        }

        if (newCustomer) {

            sendVerificationEmail(request, savedCustomer);
        }

        return listAllCustomers();

    }

    @GetMapping("/customer/edit/{id}")
    public ModelAndView editCustomer(@PathVariable("id") Integer id, RedirectAttributes ra) {
        ModelAndView model = new ModelAndView("customer/new-customer-form");
        Customer customer = service.findById(id);
        List<Country> countriesList = service.listAllCountries();
        String pageTitle = String.format("Edit Customer (ID: %d)", id);

        model.addObject("customer", customer);
        model.addObject("id", id);
        model.addObject("listItems", countriesList);
        model.addObject("pageTitle", pageTitle);
        model.addObject("saveChanges", "/customer/save-customer");

        return model;
    }

    @GetMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }

    @GetMapping("/customer/{id}/enable/{status}")
    public ModelAndView UpdateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enable,
                                         RedirectAttributes redirectAttributes) {

        service.UdpateCustomerEnableStatus(id, enable);
        String status = enable ? "enable" : " disable";
        String message = " the Customer Id  " + id + "  has bean  " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return new ModelAndView("redirect:/customer/customer");

    }

    private void savePhoto(Customer customer, MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        customer.setImage(fileName);

        Customer savedCustomer = service.saveCustomer(customer);

        String uploadDir = dirName + savedCustomer.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    @PostMapping("/deleteCustomers")
    public String deleteModels(
            @RequestParam(name = "selectedModels", required = false) List<Integer> selectedModels,
            RedirectAttributes redirectAttributes) throws CustomerNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message",
                "Customers ID: " + selectedModels + " has been successfully Deleted");

        if (selectedModels != null && !selectedModels.isEmpty()) {
            for (Integer id : selectedModels) {
                FileUploadUtil.cleanDir(service.findById(id).getImageDir());
                service.delete(id);
            }
        }

        return listAllCustomers();
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer)
            throws IOException, MessagingException {
        // Fetch email settings
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();
        String siteName = emailSettings.getSiteName();

        // Create MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set placeholders
        // content = content.replace("[[name]]", customer.getFullName());
        // content = content.replace("[[Site]]", siteName);

        // Add inline logo
        // Use ClassPathResource to load from classpath/resources/site-logo
        Resource logoResource = new ClassPathResource("site-logo/dream-logo-print0200.png");

        if (!logoResource.exists()) {
            // Fallback or skip logo if missing, avoid crashing
            System.out.println("Wait: Logo file still not found in classpath.");
        } else {
            // Set MIME type for the logo
            String contentType = URLConnection.guessContentTypeFromStream(logoResource.getInputStream());
            helper.addInline("siteLogo", logoResource, contentType);

            // Embed logo in the content
            content = content.replace("[[SITE_LOGO]]", "<img src='cid:siteLogo' alt='Site Logo'/>");
        }

        // Add verification URL
        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        // Set email details
        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        // Send the email
        mailSender.send(message);
    }

}

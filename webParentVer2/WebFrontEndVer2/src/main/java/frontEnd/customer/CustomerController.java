package frontEnd.customer;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import frontEnd.fileUtilites.utility.FileUploadUtil;
import frontEnd.security.CustomerUserDetails;
import frontEnd.security.oauth.CustomerAuthenticationHelper;
import frontEnd.setting.EmailSettingBag;
import frontEnd.setting.service.SettingService;
import frontEnd.utilites.NewFormHelper;
import frontEnd.utilites.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private CustomerAuthenticationHelper authHelper;

    public static String getFileNameFromPath(String filePath) {
        // Create a File object from the given path
        File file = new File(filePath);
        file.getAbsolutePath();
        // Return just the file name (without the path)
        return file.getName();
    }

    @GetMapping("/account")
    public ModelAndView ViewAccountDetails(@AuthenticationPrincipal CustomerUserDetails loggedUser,
            RedirectAttributes redirectAttributes) {

        Integer customerId = authHelper.getAuthenticatedCustomerId();

        ModelAndView model = new ModelAndView("register/register_form");
        String pageTitle = "Update Profile " + customerService.getFullName(customerId);

        Customer customer = customerService.findById(customerId);
        List<Country> countriesList = customerService.listAllCountries();

        NewFormHelper customerRegistration = new NewFormHelper(pageTitle, countriesList);
        return customerRegistration.editForm(model, "customer", customer, customerId);

    }

    @GetMapping("/registration")
    public ModelAndView showRegistrationForm() {
        ModelAndView model = new ModelAndView("register/register_form");
        Customer customer = new Customer();
        String pageTitle = "Customer Registration";
        List<Country> countriesList = customerService.listAllCountries();

        NewFormHelper customerRegistratian = new NewFormHelper(pageTitle, countriesList);
        return customerRegistratian.newForm(model, "customer", customer);

    }

    @org.springframework.beans.factory.annotation.Value("${app.storage.tenants-path}")
    private String tenantsPath; // Injected from AG-CONFIG-ENV-001

    @PostMapping("/save-customer")
    public ModelAndView saveNewCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes,
            HttpServletRequest request, @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException, MessagingException {

        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        System.out.println("DEBUG: saveNewCustomer - Resolving TenantID: " + tenantId);

        redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully.");

        boolean newCustomer = customer.getId() == null;

        // AG-TEN-FIX: Explicitly assign Tenant ID for registration
        customer.setTenantId(tenantId);

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            customer.setImage(fileName); // Store only filename, Entity handles path generation
        }

        Customer savedCustomer = customerService.saveCustomer(customer);

        if (!multipartFile.isEmpty()) {
            // AG-ASSET-PATH-FIX: Use standardized tenant assets path
            // Path: {tenantsPath}/{tenantId}/assets/customers/{id}
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(tenantsPath, String.valueOf(tenantId),
                    "assets/customers", String.valueOf(savedCustomer.getId()));
            String uploadDir = uploadPath.toString();

            // Clean old files (optional but good practice)
            FileUploadUtil.cleanDir(uploadDir);

            // Backend FileUploadUtil is usually static, Frontend one matches signature
            FileUploadUtil.saveFile(uploadDir, customer.getImage(), multipartFile);
        }

        if (newCustomer) {
            try {
                sendVerificationEmail(request, savedCustomer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ModelAndView("/register_success");
    }

    private void savePhoto(Customer customer, MultipartFile multipartFile, String dirName) throws IOException {
        // Deprecated helper - logic moved inline to support dynamic paths
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = dirName + customer.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    // private void sendVerificationEmail(HttpServletRequest request, Customer
    // customer) throws IOException, MessagingException {
    // // Fetch email settings
    // EmailSettingBag emailSettings = settingService.getEmailSettings();
    // JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
    //
    // String toAddress = customer.getEmail();
    // String subject = emailSettings.getCustomerVerifySubject();
    // String content = emailSettings.getCustomerVerifyContent();
    // String siteName = emailSettings.getSiteName();
    //
    // // Create MimeMessage
    // MimeMessage message = mailSender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //
    // // Set placeholders
    // content = content.replace("[[name]]", customer.getFullName());
    // content = content.replace("[[Site]]", siteName);
    //
    //
    // // Add inline logo
    //// String logoPath = ".."+emailSettings.getSiteLogo();
    //
    //
    // String logoPath = "/site-logo/dream-logo-print0200.png";
    // File file = new File(logoPath);
    //
    // System.out.println("Looking for file at: " + file.getAbsolutePath());
    // System.out.println("File exists: " + file.exists());
    //
    //// Resource logoResource = new
    // ClassPathResource("site-logo/dream-logo-print0200.png");
    //
    //// ClassPathResource logoResource = new
    // ClassPathResource(file.getAbsolutePath());
    //
    //
    //// System.out.println(" logo Resource " + logoResource);
    //// System.out.println("Current ClassPath: " +
    // System.getProperty("java.class.path"));
    //
    //
    //// if (!logoResource.exists()) {
    //// throw new FileNotFoundException("Logo file not found at: " +
    // file.getAbsolutePath());
    //// }
    //
    //
    //// // Set MIME type for the logo
    //// String contentType =
    // URLConnection.guessContentTypeFromStream(logoResource.getInputStream());
    //// helper.addInline("siteLogo", logoResource, contentType);
    //
    //
    //// System.out.println(" content Type " + contentType);
    //
    //
    // // Embed logo in the content
    // content = content.replace("[[SITE_LOGO]]", "<img src='cid:siteLogo' alt='Site
    // Logo'/>");
    //
    //
    // // Add verification URL
    // String verifyURL = Utility.getSiteURL(request) + "/verify?code=" +
    // customer.getVerificationCode();
    // content = content.replace("[[URL]]", verifyURL);
    //
    // // Set email details
    // helper.setFrom(emailSettings.getFromAddress(),
    // emailSettings.getSenderName());
    // helper.setTo(toAddress);
    // helper.setSubject(subject);
    // helper.setText(content, true);
    //
    // // Send the email
    // mailSender.send(message);
    // }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) {
        try {
            // Fetch email settings
            EmailSettingBag emailSettings = settingService.getEmailSettings();
            JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

            String toAddress = customer.getEmail();
            String subject = emailSettings.getCustomerVerifySubject();
            String content = emailSettings.getCustomerVerifyContent();
            String siteName = emailSettings.getSiteName();

            // Replace placeholders
            content = content.replace("[[name]]", customer.getFullName());
            content = content.replace("[[Site]]", siteName);
            subject = subject.replace("[[Site]]", siteName);

            // Generate verification URL
            String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
            content = content.replace("[[URL]]", verifyURL);

            // OPTIONAL: remove the logo placeholder if not used
            content = content.replace("[[SITE_LOGO]]", "/" + customer.getImagePath());

            // Create email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            // Send email
            mailSender.send(message);

            System.out.println("Verification email sent to: " + toAddress);

        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/verify")
    public ModelAndView verifyAccount(@Param("code") String code) {
        boolean verified = customerService.verify(code);

        ModelAndView model = new ModelAndView("register/" + (verified ? "verify_success" : "verify_fail"));
        model.addObject("pageTitle", (verified ? "verify_success" : "verify_fail"));
        return model;
    }

}

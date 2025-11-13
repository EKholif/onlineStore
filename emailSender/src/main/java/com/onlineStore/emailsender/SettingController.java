package com.onlineStore.emailsender;

import com.onlineStoreCom.entity.setting.Setting;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class SettingController {

    @Autowired
    private SettingService service;

    @GetMapping("/")
    public String listAll(Model modal) {

        List<Setting> settingList = service.settingList();
        for (Setting setting : settingList) {
            modal.addAttribute(setting.getKey(), setting.getValue());
        }

        return "settings/settings";

    }


    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSetttings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailServerSettings = service.getMailServerSettings();

        updateSettingValuesFromForm(request, mailServerSettings);

        ra.addFlashAttribute("message", "Mail server settings have been saved");

        return "redirect:/#mailServer";
    }


    @PostMapping("/settings/save_mail_template")
    public String saveMailTemplateSetttings(HttpServletRequest request, RedirectAttributes ra) {

        List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
        updateSettingValuesFromForm(request, mailTemplateSettings);


        ra.addFlashAttribute("message", "Mail Template have been saved");

        return "redirect:/#mailServer";
    }


    @PostMapping("/settings/send_mail_template")
    public String sendMailTemplateSetttings(HttpServletRequest request, RedirectAttributes ra) throws MessagingException, UnsupportedEncodingException {

        List<Setting> mailServerSettings = service.getMailServerSettings();

        String email = request.getParameter("email");

           emailSender(request);



        ra.addFlashAttribute("message", "Mail Template have been saved");

        return "redirect:/#mailServer";
    }


    private void emailSender(HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = service.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = request.getParameter("E-mail");

        String subject = request.getParameter("Subject");
        String content = request.getParameter("Content");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
//        String orderTime = dateFormatter.format(order.getOrderTime());



//        content = content.replace("[[name]]", order.getCustomer().getFullName());
//        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
//        content = content.replace("[[orderTime]]", orderTime);
//        content = content.replace("[[shippingAddress]]",shippingAddress);
//        content = content.replace("[[total]]", totalAmount);
//        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);

            mailSender.send(message);

    }


}
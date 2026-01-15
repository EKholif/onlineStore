package com.onlineStore.emailsender;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;



public class EmailSender {


    private SettingService settingService;

    private void emailSender(HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = request.getParameter("E-mail");
        String subject = request.getParameter("Subject");
        String content = request.getParameter("Content");

//        String shippingAddress =   order.getShippingAddress();


//        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

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

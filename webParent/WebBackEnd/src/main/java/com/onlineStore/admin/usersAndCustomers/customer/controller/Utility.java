package com.onlineStore.admin.usersAndCustomers.customer.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class Utility {

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();

        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settings.getHost());
        mailSender.setPort(settings.getPort());
        mailSender.setUsername(settings.getUsername());
        mailSender.setPassword(settings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

//    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
//        Object principal = request.getUserPrincipal();
//        if (principal == null) return null;
//
//        String customerEmail = null;
//
//        if (principal instanceof UsernamePasswordAuthenticationToken
//                || principal instanceof RememberMeAuthenticationToken) {
//            customerEmail = request.getUserPrincipal().getName();
//        } else if (principal instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
//            CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
//            customerEmail = oauth2User.getEmail();
//        }
//
//        return customerEmail;
//    }
//
//    public static String formatCurrency(float amount, CurrencySettingBag settings) {
//        String symbol = settings.getSymbol();
//        String symbolPosition = settings.getSymbolPosition();
//        String decimalPointType = settings.getDecimalPointType();
//        String thousandPointType = settings.getThousandPointType();
//        int decimalDigits = settings.getDecimalDigits();
//
//        String pattern = symbolPosition.equals("Before price") ? symbol : "";
//        pattern += "###,###";
//
//        if (decimalDigits > 0) {
//            pattern += ".";
//            for (int count = 1; count <= decimalDigits; count++) pattern += "#";
//        }
//
//        pattern += symbolPosition.equals("After price") ? symbol : "";
//
//        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
//        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';
//
//        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
//        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
//        decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
//
//        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
//
//        return formatter.format(amount);
//    }
}

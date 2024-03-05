package com.onlineStore.admin.abstarct;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractExporter {


    public void export(HttpServletResponse response, String contentType, String extinction,String prefix) throws IOException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormatter.format(new Date());
        String fileName =  prefix + timeStamp + extinction;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);


    }
}
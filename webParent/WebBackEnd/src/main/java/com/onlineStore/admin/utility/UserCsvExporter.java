package com.onlineStore.admin.utility;

import com.onlineStoreCom.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

       super.export(response,"text/csv",".csv");


        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String [] csvHeader = {"User ID", "First Name", "Last Name","E-mail", " Roles"," status"};
        String [] fieldMapping = {"id", "firstName", "lastName","email" ,"roles","enable"};
        csvBeanWriter.writeHeader(csvHeader);

        for (User user: listUsers) {

              csvBeanWriter.write(user,fieldMapping);
        }

        csvBeanWriter.close();


    }






}

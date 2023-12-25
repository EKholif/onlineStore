package com.onlineStore.admin.category.controller.utility;

import com.onlineStoreCom.entity.prodact.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvCategoryExporter extends AbstractCategoryExporter {

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {

       super.export(response,"text/csv",".csv");


        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String [] csvHeader = {"ID", "Name", "Alies","Parent", "Children"," status"};
        String [] fieldMapping = {"id", "name", "alias","parent" ,"children","enable"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Category category: categoryList) {

              csvBeanWriter.write(category,fieldMapping);
        }

        csvBeanWriter.close();


    }






}

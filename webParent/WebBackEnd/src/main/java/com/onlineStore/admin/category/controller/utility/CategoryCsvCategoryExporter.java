package com.onlineStore.admin.category.controller.utility;

import com.onlineStore.admin.abstarct.AbstractExporter;
import com.onlineStoreCom.entity.category.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvCategoryExporter extends AbstractExporter {

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {

        super.export(response, "text/csv", ".csv", "Category_");


        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID  Name", "Alies", "Parent", "Children", " status"};
        String[] fieldMapping = {"name", "alias", "parent", "children", "enable"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Category category : categoryList) {
            category.setName(category.getName().replace("--", "  "));
            csvBeanWriter.write(category, fieldMapping);
        }

        csvBeanWriter.close();


    }

}

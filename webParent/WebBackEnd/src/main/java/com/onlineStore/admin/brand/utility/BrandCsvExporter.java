package com.onlineStore.admin.brand.utility;

import com.onlineStore.admin.abstarct.AbstractExporter;
import com.onlineStoreCom.entity.brand.Brand;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {

    public void export(List<Brand> brandListList, HttpServletResponse response) throws IOException {

        super.export(response, "text/csv", ".csv", "Category_");


        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID  Name Categories"};
        String[] fieldMapping = {"id", "name", "categories"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Brand brand : brandListList) {
            brand.setName(brand.getName().replace("--", "  "));
            csvBeanWriter.write(brand, fieldMapping);
        }

        csvBeanWriter.close();


    }

}

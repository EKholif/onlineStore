package com.onlineStore.admin.utility;

import com.onlineStoreCom.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {


    XSSFWorkbook workbook ;
    XSSFSheet sheet ;

    public UserExcelExporter() {
        workbook = new  XSSFWorkbook();
    }

    private void writeHeadline(){
        sheet = workbook.createSheet("Users");
        XSSFRow row=sheet.createRow(0);

        XSSFCellStyle cellStyle=workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row ,0 ,"ID" ,cellStyle );
        createCell(row ,1 ,"First Name" ,cellStyle );
        createCell(row ,2 ,"Last Name" ,cellStyle );
        createCell(row ,3 ,"Email" ,cellStyle );
        createCell(row ,4 ,"Roles" ,cellStyle );
        createCell(row ,5 ,"Status" ,cellStyle );


    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style){
        XSSFCell cell = row.createCell(columnIndex);

        if (value instanceof Integer){
            cell.setCellValue((Integer) value);
        }

       else if (value instanceof Boolean){
            cell.setCellValue((Boolean)value);
        }
        else if (value instanceof Long){
            cell.setCellValue((Long)value);
        }

        else {
            cell.setCellValue((String) value);
        }

         cell.setCellStyle(style);
    }



    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

        super.export(response, "application/octet-stream", ".xlsx");

        writeHeadline();
        writeDatelines(listUsers);


        ServletOutputStream outputStream =response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private void writeDatelines(List<User> listUsers) {
        int rowIndex =1;
        XSSFCellStyle cellStyle=workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight(14);
        cellStyle.setFont(font);



        for (User user: listUsers){
          XSSFRow row=  sheet.createRow(rowIndex++);
          int columnIndex=0;
            createCell(row ,0 ,user.getId() ,cellStyle );
            createCell(row ,1 ,user.getfirstName() ,cellStyle );
            createCell(row ,2 ,user.getLastName() ,cellStyle );
            createCell(row ,3 ,user.getEmail() ,cellStyle );
            createCell(row ,4 ,user.getRoles().toString() ,cellStyle );
            createCell(row ,5 ,user.isEnable() ,cellStyle );
        }
    }
}
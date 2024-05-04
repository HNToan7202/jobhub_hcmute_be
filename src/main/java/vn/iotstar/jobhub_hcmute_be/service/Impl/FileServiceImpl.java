package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.NewUserDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class FileServiceImpl {
    public byte[] downloadNewUserList(List<NewUserDTO> userDTOs, String link) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Workbook workbook = new SXSSFWorkbook(100)) { // Create new workbook
            Sheet sheet = workbook.createSheet("NewUsers");
            // Tạo một hàng mới để chứa ngày in
            Row dateRow = sheet.createRow(0);
            LocalDate currentDate = LocalDate.now();
            Date endOfDayYesterday = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateRow.createCell(0).setCellValue("Print Date: " + endOfDayYesterday.toString());
            Row headerRow = sheet.createRow(1);
            String[] headers = {"UserId", "Email", "Phone", "FullName", "Avatar", "IsVerified", "IsActive", "CreatedAt", "LastLoginAt","Link"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowNum = 2;
            for (NewUserDTO userDTO : userDTOs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(userDTO.getUserId());
                row.createCell(1).setCellValue(userDTO.getEmail());
                row.createCell(2).setCellValue(userDTO.getPhone());
                row.createCell(3).setCellValue(userDTO.getFullName());
                row.createCell(4).setCellValue(userDTO.getAvatar());
                row.createCell(5).setCellValue(userDTO.isVerified());
                row.createCell(6).setCellValue(userDTO.isActive());
                row.createCell(7).setCellValue(userDTO.getCreatedAt().toString());
                row.createCell(8).setCellValue(userDTO.getLastLoginAt()==null?"":userDTO.getLastLoginAt().toString());
                row.createCell(9).setCellValue(link + userDTO.getUserId());
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


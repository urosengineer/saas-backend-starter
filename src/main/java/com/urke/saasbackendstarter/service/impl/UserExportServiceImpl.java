package com.urke.saasbackendstarter.service.impl;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;
import com.urke.saasbackendstarter.repository.UserRepository;
import com.urke.saasbackendstarter.service.UserExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserExportServiceImpl implements UserExportService {

    private final UserRepository userRepository;

    @Override
    public List<User> findExportUsers(Organization org, String email) {
        if (StringUtils.hasText(email)) {
            return userRepository.findAllByOrganizationAndDeletedFalse(org).stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByOrganizationAndDeletedFalse(org);
    }

    @Override
    public byte[] exportToExcel(List<User> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Email");
            header.createCell(2).setCellValue("Full Name");
            header.createCell(3).setCellValue("Roles");
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getFullName());
                row.createCell(3).setCellValue(
                        user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining(", "))
                );
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Excel export failed", e);
        }
    }

    @Override
    public byte[] exportToPdf(List<User> users) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph title = new Paragraph("Users Export", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new int[]{1, 4, 4, 3});
            table.setSpacingBefore(10);

            String[] headers = {"ID", "Email", "Full Name", "Roles"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (User user : users) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getEmail());
                table.addCell(user.getFullName());
                table.addCell(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining(", ")));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "PDF export failed", e);
        }
    }
}
package com.algomentor.service;

import com.algomentor.dto.StudentProgressDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ReportService {

    public byte[] generateStudentProgressCSV(List<StudentProgressDTO> students) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader("Name", "Email", "Roll Number", "Section", "Group", "Total Solved", "Easy", "Medium", "Hard"))) {
            
            for (StudentProgressDTO student : students) {
                printer.printRecord(
                    student.getStudentName(),
                    student.getEmail(),
                    student.getRollNumber(),
                    student.getSection(),
                    student.getGroup(),
                    student.getLeetCodeTotal(),
                    student.getLeetCodeEasy(),
                    student.getLeetCodeMedium(),
                    student.getLeetCodeHard()
                );
            }
            printer.flush();
            return out.toByteArray();
        }
    }

    public byte[] generateStudentProgressPDF(List<StudentProgressDTO> students, String section) throws DocumentException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Student Progress Report - Section " + section, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(9); // Increased columns to 9
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 4, 2, 2, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f}); // Adjusted widths

            // Headers
            String[] headers = {"Name", "Email", "Roll No", "Section", "Group", "Total", "Easy", "Med", "Hard"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Data
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            
            for (StudentProgressDTO student : students) {
                addCell(table, student.getStudentName(), dataFont);
                addCell(table, student.getEmail(), dataFont);
                addCell(table, student.getRollNumber() != null ? student.getRollNumber() : "N/A", dataFont);
                addCell(table, student.getSection() != null ? student.getSection() : "N/A", dataFont);
                addCell(table, student.getGroup() != null ? student.getGroup() : "N/A", dataFont);
                addCell(table, String.valueOf(student.getLeetCodeTotal()), dataFont);
                addCell(table, String.valueOf(student.getLeetCodeEasy()), dataFont);
                addCell(table, String.valueOf(student.getLeetCodeMedium()), dataFont);
                addCell(table, String.valueOf(student.getLeetCodeHard()), dataFont);
            }

            document.add(table);
            document.close();
            
            return out.toByteArray();
        }
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);
        table.addCell(cell);
    }
}

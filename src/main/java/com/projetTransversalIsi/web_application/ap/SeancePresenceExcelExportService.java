package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.web_application.ap.dto.SeanceApDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeancePresenceEtudiantDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class SeancePresenceExcelExportService {

    // Colonnes fixes : N°, MATRICULE, NOM(S) ET PRENOM(S)
    private static final int FIXED_COLS = 3;

    public byte[] buildExcel(SeanceApDTO seance, List<SeancePresenceEtudiantDTO> etudiants) throws IOException {
        List<String> creneaux = HeureSlotHelper.computeCreneaux(seance.heureDebut(), seance.heureFin());
        int totalCols = FIXED_COLS + Math.max(creneaux.size(), 1);

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Présence");

            // ── Styles ────────────────────────────────────────────────────────
            CellStyle titleStyle = style(wb, IndexedColors.PALE_BLUE, true, 12,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);

            CellStyle infoStyle = style(wb, null, false, 10,
                    HorizontalAlignment.LEFT, BorderStyle.NONE);

            CellStyle headerStyle = style(wb, IndexedColors.GREY_25_PERCENT, true, 9,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);
            headerStyle.setWrapText(true);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle dataStyle = dataStyle(wb, null);
            CellStyle dataLeft = dataStyle(wb, null);
            dataLeft.setAlignment(HorizontalAlignment.LEFT);

            CellStyle presentStyle = coloredTextStyle(wb, IndexedColors.GREEN);
            CellStyle absentStyle = coloredTextStyle(wb, IndexedColors.RED);

            // ── Ligne 0 : titre ─────────────────────────────────────────────
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(22);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Liste de présence — " + (seance.libelle() != null ? seance.libelle() : "Séance"));
            titleCell.setCellStyle(titleStyle);
            for (int c = 1; c < totalCols; c++) titleRow.createCell(c).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCols - 1));

            // ── Ligne 1 : informations séance ────────────────────────────────
            Row infoRow = sheet.createRow(1);
            infoRow.setHeightInPoints(18);
            String horaire = formatTime(seance.heureDebut()) + " – " + formatTime(seance.heureFin());
            String enseignant = (seance.enseignantNom() != null || seance.enseignantPrenom() != null)
                    ? (nullToEmpty(seance.enseignantPrenom()) + " " + nullToEmpty(seance.enseignantNom())).trim()
                    : "—";
            String infoText = "Date : " + seance.dateSeance()
                    + "   |   Horaire : " + horaire
                    + "   |   Classe : " + nullToEmpty(seance.classeCode())
                    + "   |   Salle : " + (seance.salle() != null ? seance.salle() : "—")
                    + "   |   Enseignant : " + enseignant;
            Cell infoCell = infoRow.createCell(0);
            infoCell.setCellValue(infoText);
            infoCell.setCellStyle(infoStyle);
            for (int c = 1; c < totalCols; c++) infoRow.createCell(c).setCellStyle(infoStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalCols - 1));

            // ── Ligne 2 : statistiques ──────────────────────────────────────
            long total = etudiants.size();
            long presents = etudiants.stream().filter(e -> Boolean.TRUE.equals(e.present())).count();
            long absents = total - presents;
            double taux = total > 0 ? Math.round(presents * 1000.0 / total) / 10.0 : 0;

            Row statsRow = sheet.createRow(2);
            statsRow.setHeightInPoints(18);
            String statsText = "Présents : " + presents
                    + "   |   Absents : " + absents
                    + "   |   Total : " + total
                    + "   |   Taux de présence : " + taux + " %";
            Cell statsCell = statsRow.createCell(0);
            statsCell.setCellValue(statsText);
            statsCell.setCellStyle(infoStyle);
            for (int c = 1; c < totalCols; c++) statsRow.createCell(c).setCellStyle(infoStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, totalCols - 1));

            // ── Ligne 4 : en-têtes des colonnes ─────────────────────────────
            int headerRowIdx = 4;
            Row headerRow = sheet.createRow(headerRowIdx);
            headerRow.setHeightInPoints(28);

            String[] fixedHeaders = { "N°", "MATRICULE", "NOM(S) ET PRENOM(S)" };
            for (int c = 0; c < FIXED_COLS; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(fixedHeaders[c]);
                cell.setCellStyle(headerStyle);
            }
            int nbCreneauxHeader = Math.max(creneaux.size(), 1);
            for (int i = 0; i < nbCreneauxHeader; i++) {
                cell(headerRow, FIXED_COLS + i, i + 1, headerStyle);
            }

            // ── Lignes de données ───────────────────────────────────────────
            int nbCreneaux = Math.max(creneaux.size(), 1);
            for (int si = 0; si < etudiants.size(); si++) {
                SeancePresenceEtudiantDTO e = etudiants.get(si);
                Row row = sheet.createRow(headerRowIdx + 1 + si);
                row.setHeightInPoints(18);

                cell(row, 0, si + 1, dataStyle);
                cell(row, 1, e.matricule() != null ? e.matricule() : "—", dataStyle);
                cell(row, 2, nullToEmpty(e.prenom()) + " " + nullToEmpty(e.nom()), dataLeft);

                boolean present = Boolean.TRUE.equals(e.present());
                CellStyle pStyle = present ? presentStyle : absentStyle;
                String label = present ? "P" : "A";
                for (int i = 0; i < nbCreneaux; i++) {
                    cell(row, FIXED_COLS + i, label, pStyle);
                }
            }

            // ── Largeurs de colonnes ────────────────────────────────────────
            sheet.setColumnWidth(0, 1400);   // N°
            sheet.setColumnWidth(1, 4200);   // Matricule
            sheet.setColumnWidth(2, 9500);   // Nom et Prénom
            for (int i = 0; i < nbCreneaux; i++) {
                sheet.setColumnWidth(FIXED_COLS + i, 2800);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }

    private String formatTime(java.time.LocalTime time) {
        return time != null ? String.format("%02dh%02d", time.getHour(), time.getMinute()) : "—";
    }

    private CellStyle style(XSSFWorkbook wb, IndexedColors bg, boolean bold, int fontSize,
                             HorizontalAlignment align, BorderStyle border) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(bold);
        f.setFontHeightInPoints((short) fontSize);
        s.setFont(f);
        s.setAlignment(align);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bg != null) {
            s.setFillForegroundColor(bg.getIndex());
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        s.setBorderTop(border); s.setBorderBottom(border);
        s.setBorderLeft(border); s.setBorderRight(border);
        return s;
    }

    private CellStyle coloredTextStyle(XSSFWorkbook wb, IndexedColors fontColor) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 10);
        f.setColor(fontColor.getIndex());
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private CellStyle dataStyle(XSSFWorkbook wb, IndexedColors bg) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bg != null) {
            s.setFillForegroundColor(bg.getIndex());
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private void cell(Row row, int col, int value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private void cell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }
}

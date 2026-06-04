package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.web_application.ap.repository.ExportAbsenceUeRow;
import com.projetTransversalIsi.web_application.ap.repository.ExportStudentRow;
import com.projetTransversalIsi.web_application.ap.repository.ExportUeInfoRow;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPAbsencesRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AbsenceExcelExportService {

    private final SpringDataAPAbsencesRepository absencesRepo;

    // Colonnes fixes : N°, MATRICULE, NOM(S) ET PRENOM(S)
    private static final int FIXED_COLS  = 3;
    // Colonnes par UE : Total Absences | Total Justifié | Total Pén. | Malus sur CC
    private static final int COLS_PER_UE = 4;

    public byte[] buildExcel(Long classeId, Long filiereId) throws IOException {
        List<ExportStudentRow>    students = absencesRepo.findStudentsInClasseForExport(classeId, filiereId);
        List<ExportUeInfoRow>     ues      = absencesRepo.findUesForExport(classeId);
        List<ExportAbsenceUeRow>  absences = absencesRepo.findAbsencesParUeForExport(classeId);

        Map<String, double[]> absMap = new HashMap<>();
        for (ExportAbsenceUeRow r : absences) {
            absMap.put(r.getEtudiantId() + "_" + r.getUeId(),
                       new double[]{ r.getHeuresAbsence(), r.getHeuresJustifiees() });
        }

        String classeCode = students.isEmpty() ? "Classe" : students.get(0).getClasseCode();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Absences - " + classeCode);

            // ── Styles ────────────────────────────────────────────────────────

            // Ligne 0 — nom de la matière (fond saumon, centré, gras)
            CellStyle ueNameStyle = style(wb, IndexedColors.ROSE, true, 10,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);
            ueNameStyle.setWrapText(true);

            // Ligne 1 — libellé info (fond beige clair)
            CellStyle infoLabelStyle = style(wb, IndexedColors.TAN, false, 9,
                    HorizontalAlignment.LEFT, BorderStyle.THIN);
            infoLabelStyle.setWrapText(true);

            // Ligne 1 — valeur volume horaire (fond saumon foncé, gras)
            CellStyle infoVolStyle = style(wb, IndexedColors.CORAL, true, 10,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);

            // Ligne 1 — valeur seuil (fond orange, gras)
            CellStyle infoSeuilStyle = style(wb, IndexedColors.LIGHT_ORANGE, true, 10,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);

            // Ligne 2 — en-tête colonnes fixes (N°, Matricule, Nom)
            CellStyle fixedHeaderStyle = style(wb, IndexedColors.GREY_25_PERCENT, true, 9,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);
            fixedHeaderStyle.setWrapText(true);
            fixedHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Ligne 2 — en-tête colonnes UE
            CellStyle colHeaderStyle = style(wb, IndexedColors.PALE_BLUE, true, 9,
                    HorizontalAlignment.CENTER, BorderStyle.THIN);
            colHeaderStyle.setWrapText(true);
            colHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Données — cellule normale
            CellStyle dataStyle   = dataStyle(wb, null);
            // Données — nom (aligné à gauche)
            CellStyle dataLeft    = dataStyle(wb, null);
            dataLeft.setAlignment(HorizontalAlignment.LEFT);
            // Données — cellule avec pénalité (fond rouge)
            CellStyle penStyle    = dataStyle(wb, IndexedColors.ROSE);

            // ── Ligne 0 : nom de la matière ────────────────────────────────
            Row ueNameRow = sheet.createRow(0);
            ueNameRow.setHeightInPoints(30);

            for (int c = 0; c < FIXED_COLS; c++) ueNameRow.createCell(c); // vide

            for (int i = 0; i < ues.size(); i++) {
                ExportUeInfoRow ue = ues.get(i);
                int start = FIXED_COLS + i * COLS_PER_UE;
                String label = (ue.getCode() != null ? ue.getCode() + " — " : "") + ue.getLibelle();
                Cell c = ueNameRow.createCell(start);
                c.setCellValue(label);
                c.setCellStyle(ueNameStyle);
                for (int k = start + 1; k < start + COLS_PER_UE; k++)
                    ueNameRow.createCell(k).setCellStyle(ueNameStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, start, start + COLS_PER_UE - 1));
            }

            // ── Ligne 1 : infos (Total Heures EC | valeur | Seuil Tolérance | valeur) ──
            Row infoRow = sheet.createRow(1);
            infoRow.setHeightInPoints(24);

            for (int c = 0; c < FIXED_COLS; c++) infoRow.createCell(c); // vide

            for (int i = 0; i < ues.size(); i++) {
                ExportUeInfoRow ue = ues.get(i);
                int start = FIXED_COLS + i * COLS_PER_UE;
                double vol   = ue.getVolumeHoraireTotal() != null ? ue.getVolumeHoraireTotal() : 0;
                int    seuil = (int) Math.ceil(vol * 0.1);

                // offset 0 — libellé volume horaire
                Cell lblVol = infoRow.createCell(start);
                lblVol.setCellValue("Total Heures EC (H)");
                lblVol.setCellStyle(infoLabelStyle);

                // offset 1 — valeur volume horaire
                Cell valVol = infoRow.createCell(start + 1);
                valVol.setCellValue((int) Math.round(vol));
                valVol.setCellStyle(infoVolStyle);

                // offset 2 — libellé seuil
                Cell lblSeuil = infoRow.createCell(start + 2);
                lblSeuil.setCellValue("Seuil Tolérance (HP)");
                lblSeuil.setCellStyle(infoLabelStyle);

                // offset 3 — valeur seuil (orange)
                Cell valSeuil = infoRow.createCell(start + 3);
                valSeuil.setCellValue(seuil);
                valSeuil.setCellStyle(infoSeuilStyle);
            }

            // ── Ligne 2 : en-têtes des colonnes ────────────────────────────
            Row headerRow = sheet.createRow(2);
            headerRow.setHeightInPoints(15);

            String[] fixedHeaders = { "N°", "MATRICULE", "NOM(S) ET PRENOM(S)" };
            for (int c = 0; c < FIXED_COLS; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(fixedHeaders[c]);
                cell.setCellStyle(fixedHeaderStyle);
            }

            String[] ueSubHeaders = {
                "Total Absences (H)",
                "Total Justifié (H)",
                "Total Pén. (H)",
                "Malus sur CC"
            };
            for (int i = 0; i < ues.size(); i++) {
                int start = FIXED_COLS + i * COLS_PER_UE;
                for (int j = 0; j < COLS_PER_UE; j++) {
                    Cell cell = headerRow.createCell(start + j);
                    cell.setCellValue(ueSubHeaders[j]);
                    cell.setCellStyle(colHeaderStyle);
                }
            }

            // ── Lignes de données ──────────────────────────────────────────
            for (int si = 0; si < students.size(); si++) {
                ExportStudentRow stu = students.get(si);
                Row row = sheet.createRow(3 + si);
                row.setHeightInPoints(18);

                cell(row, 0, si + 1,                              dataStyle);
                cell(row, 1, stu.getMatricule(),                  dataStyle);
                cell(row, 2, stu.getPrenom() + " " + stu.getNom(), dataLeft);

                for (int i = 0; i < ues.size(); i++) {
                    ExportUeInfoRow ue    = ues.get(i);
                    int             start = FIXED_COLS + i * COLS_PER_UE;

                    double vol    = ue.getVolumeHoraireTotal() != null ? ue.getVolumeHoraireTotal() : 0;
                    double[] abs  = absMap.getOrDefault(stu.getEtudiantId() + "_" + ue.getUeId(),
                                                        new double[]{0, 0});
                    int heuresAbs     = (int) Math.round(abs[0]);
                    int heuresJust    = (int) Math.round(abs[1]);
                    int heuresNonJust = Math.max(0, heuresAbs - heuresJust);
                    int seuil         = (int) Math.ceil(vol * 0.1);
                    int heuresPen     = Math.max(0, heuresNonJust - seuil);
                    int malus         = heuresPen;

                    cell(row, start,     heuresAbs,               heuresAbs  > 0 ? penStyle : dataStyle);
                    cell(row, start + 1, heuresJust,               dataStyle);
                    cell(row, start + 2, heuresPen,                heuresPen  > 0 ? penStyle : dataStyle);
                    cell(row, start + 3, malus > 0 ? -malus : 0,  malus      > 0 ? penStyle : dataStyle);
                }
            }

            // ── Largeurs de colonnes ──────────────────────────────────────
            sheet.setColumnWidth(0, 1400);   // N°
            sheet.setColumnWidth(1, 4200);   // Matricule
            sheet.setColumnWidth(2, 9500);   // Nom et Prénom
            for (int i = 0; i < ues.size(); i++) {
                int start = FIXED_COLS + i * COLS_PER_UE;
                sheet.setColumnWidth(start,     4000); // Total Absences / "Total Heures EC"
                sheet.setColumnWidth(start + 1, 3000); // Total Justifié / valeur vol.h.
                sheet.setColumnWidth(start + 2, 4000); // Total Pén.     / "Seuil Tolérance"
                sheet.setColumnWidth(start + 3, 3000); // Malus           / valeur seuil
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

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

package com.top.utils;

import com.top.domain.MapperInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Stream;

public class ExcelUtil {

    /**
     * export excel to local
     * @param mapperInfos the data to export
     * @param path
     */
    public static void exportExcel(List<MapperInfo> mapperInfos, String path) throws Exception {

        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        // create sheet1
        Sheet sheet = wb.createSheet("MapperInfo");

        MapperInfo[] mapperInfos1 = new MapperInfo[mapperInfos.size()];
        mapperInfos.toArray(mapperInfos1);
        int sumRow = Stream.of(mapperInfos1).mapToInt(MapperInfo::getMaxLength).sum();

        // create row and cell
        ExcelUtil.createRowAndCell(sheet, 0, sumRow + 1, 0, 3);

        // create table header
        CellStyle headStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headStyle.setFont(font);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        headStyle.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        cell.setCellValue("MapperName");
        cell.setCellStyle(headStyle);

        cell = row.getCell(1);
        cell.setCellValue("Procedure");
        cell.setCellStyle(headStyle);

        cell = row.getCell(2);
        cell.setCellValue("Table");
        cell.setCellStyle(headStyle);


        // set column width
        sheet.setColumnWidth(0, 36 * 256);
        sheet.setColumnWidth(1, 36 * 256);
        sheet.setColumnWidth(2, 36 * 256);

        // create mapperName style setting first column
        CellStyle mapperNameStyle = wb.createCellStyle();
        mapperNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // create content basic style
        CellStyle contentStyle = wb.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.LEFT);

        int startRow = 1;
        int count = 0;
        for (MapperInfo mapperInfo : mapperInfos) {
            if (mapperInfo.getMaxLength() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + mapperInfo.getMaxLength()-1, 0, 0));
            }
            Cell mapperNameCell = sheet.getRow(startRow).getCell(0);
            mapperNameCell.setCellValue(mapperInfo.getMapperName());
            mapperNameCell.setCellStyle(zebraStyle(wb,mapperNameStyle,count));

            HashSet<String> procSet = mapperInfo.getProcSet();
            String[] procArr = new String[procSet.size()];
            procArr = procSet.toArray(procArr);

            HashSet<String> tableSet = mapperInfo.getTableSet();
            String[] tableArr = new String[tableSet.size()];
            tableArr = tableSet.toArray(tableArr);

            Arrays.sort(tableArr);
            Arrays.sort(procArr);

            for (int i = startRow; i < startRow + mapperInfo.getMaxLength(); i++) {

                Row currentRow = sheet.getRow(i);
                Cell cell2 = currentRow.getCell(1);
                cell2.setCellValue(i < (startRow + procArr.length)?procArr[i - startRow]:"");
                cell2.setCellStyle(zebraStyle(wb,contentStyle,count));
                Cell cell3 = currentRow.getCell(2);
                cell3.setCellValue(i < (startRow + tableArr.length)?tableArr[i - startRow]:"");
                cell3.setCellStyle(zebraStyle(wb,contentStyle,count));
            }
            startRow += mapperInfo.getMaxLength();
            count++;
        }
        sheet.setDisplayGridlines(true);

        // create sheet2
        Sheet sheet2 = wb.createSheet("TableSet");
        sheet2.setColumnWidth(0, 36 * 256);

        HashSet<String> hashSet = new HashSet<>();
        for (MapperInfo mapperInfo : mapperInfos) {
            hashSet.addAll(mapperInfo.getTableSet());
        }

        String[] tableArr = new String[hashSet.size()];
        tableArr = hashSet.toArray(tableArr);

        Arrays.sort(tableArr);

        ExcelUtil.createRowAndCell(sheet2, 0, hashSet.size() + 1, 0, 1);

        row = sheet2.getRow(0);
        cell = row.getCell(0);
        cell.setCellValue("TableName");
        cell.setCellStyle(headStyle);

        startRow = 1;
        for (int i = startRow; i < startRow + tableArr.length; i++) {

            Row currentRow = sheet2.getRow(i);
            Cell cell1 = currentRow.getCell(0);
            cell1.setCellValue(tableArr[i - startRow]);
        }
        sheet2.setDisplayGridlines(true);

        // create sheet3
        Sheet sheet3 = wb.createSheet("ProcSet");
        sheet3.setColumnWidth(0, 36 * 256);

        hashSet = new HashSet<>();
        for (MapperInfo mapperInfo : mapperInfos) {
            hashSet.addAll(mapperInfo.getProcSet());
        }

        tableArr = new String[hashSet.size()];
        tableArr = hashSet.toArray(tableArr);

        Arrays.sort(tableArr);

        ExcelUtil.createRowAndCell(sheet3, 0, hashSet.size() + 1, 0, 1);

        row = sheet3.getRow(0);
        cell = row.getCell(0);
        cell.setCellValue("ProcName");
        cell.setCellStyle(headStyle);

        startRow = 1;
        for (int i = startRow; i < startRow + tableArr.length; i++) {

            Row currentRow = sheet3.getRow(i);
            Cell cell1 = currentRow.getCell(0);
            cell1.setCellValue(tableArr[i - startRow]);
        }
        sheet3.setDisplayGridlines(true);

        FileOutputStream outFile = new FileOutputStream(path);
        wb.write(outFile);
        outFile.close();
    }

    /**
     * create row ana cell in the sheet
     */
    private static void createRowAndCell(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        for (int i = startRow; i < endRow; i++) {
            Row row = sheet.createRow(i);
            for (int j = startCol; j < endCol; j++) {
                row.createCell(j);
            }
        }
    }

    /**
     * zebra style
     * @param wb SXSSFWorkbook Object
     * @param basicStyle the basic style
     * @param index judge which color will be filled
     * @return
     */
    private static CellStyle zebraStyle(SXSSFWorkbook wb, CellStyle basicStyle, int index) {
        short color1Index = IndexedColors.LIGHT_TURQUOISE.getIndex();
        short color2Index = IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex();
        short borderColor = IndexedColors.GREY_50_PERCENT.getIndex();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.cloneStyleFrom(basicStyle);
        // bottom border
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(borderColor);
        // left border
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(borderColor);
        // top border
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(borderColor);
        // right border
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(borderColor);

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(index % 2 == 0 ? color1Index : color2Index);
        return cellStyle;
    }
}

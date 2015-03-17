package com.deere.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.deere.data.ExcelData;

public class ReadExcel {

	public List<ExcelData> readExcel(XSSFWorkbook workbook, Map<String, String> columnNameMapping) throws Exception {

		List<ExcelData> pdfData = new ArrayList<ExcelData>();
		try {
			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheet("SnapshotSheet");

			//Iterate through each rows one by one
			XSSFRow headerRow = sheet.getRow(sheet.getFirstRowNum());
			Map<String, Integer> columnIndexMappings = getColumnIndexMapping(headerRow);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				ExcelData excelData = new ExcelData();
				pdfData.add(excelData);
				XSSFRow row = sheet.getRow(i);
				populateExcelDataForRow(columnIndexMappings, excelData, row,columnNameMapping);
			}
		} catch (Exception e) {
			System.out.println("Error while reading the excel sheet.../n Please check if all column name are correct.");
			e.printStackTrace();
			throw e;
		}
		return pdfData;
	}

	private void populateExcelDataForRow(Map<String, Integer> columnIndexMappings, ExcelData excelData, XSSFRow row,
			Map<String, String> columnNameMapping) {


        excelData.AccountflexCode = getColumnValue(columnIndexMappings, row,columnNameMapping.get("3P Code"));
        excelData.State = getColumnValue(columnIndexMappings, row, columnNameMapping.get("State"));
        excelData.Nameofdealership = getColumnValue(columnIndexMappings, row, columnNameMapping.get("Dealership Name"));
        excelData.City = getColumnValue(columnIndexMappings, row, columnNameMapping.get("City"));
        excelData.DateofBirth = getColumnValue(columnIndexMappings, row, columnNameMapping.get("DOB"));
        excelData.To = getColumnValue(columnIndexMappings, row, columnNameMapping.get("To"));
        excelData.CC = getColumnValue(columnIndexMappings, row, columnNameMapping.get("CC"));
        excelData.OutputFileFolderPath = getColumnValue(columnIndexMappings, row, columnNameMapping.get("Output File Folder Path"));
        excelData.OutputFileName = getColumnValue(columnIndexMappings, row, columnNameMapping.get("Output File Name"));
        excelData.PANNo = getColumnValue(columnIndexMappings, row, columnNameMapping.get("Pan Number"));
        excelData.SAPCodePAG = getColumnValue(columnIndexMappings, row, columnNameMapping.get("SAP Code (PAG)"));
		excelData.TINNoasperregcertificate = getColumnValue(columnIndexMappings, row,
                columnNameMapping.get("TIN No. as per reg certificate"));
	}

	private Map<String, Integer> getColumnIndexMapping(XSSFRow headerRow) {
		Map<String, Integer> columnIndexMappings = new HashMap();
		int columnIndex = 0;
		Iterator<Cell> headerRowIterator = headerRow.cellIterator();
		while (headerRowIterator.hasNext()) {
			columnIndexMappings.put(headerRowIterator.next().getStringCellValue(), columnIndex);
			columnIndex++;
		}
		return columnIndexMappings;
	}

	private String getColumnValue(Map<String, Integer> columnIndexMappings, XSSFRow row, String key) {
		XSSFCell cell = row.getCell(columnIndexMappings.get(key));
		return getValueInCell(cell);
	}

	public static String getValueInCell(Cell cell) {
		String value = null;
		//Check the cell type and format accordingly
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				value = new Integer((int) cell.getNumericCellValue()).toString();
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
		}
		return value;
	}


    public Map<String,String> getColumnNameMapping(XSSFWorkbook workbook) throws Exception {

        Map<String, String> columnNameMappings = new HashMap();
        try {
	        XSSFSheet sheet = workbook.getSheet("Configuration");

            Iterator<Row> rowIterator = sheet.rowIterator();
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                String variable = getValueInCell(currentRow.getCell(0));
                String columnNameInExcel = getValueInCell(currentRow.getCell(1));

                columnNameMappings.put(variable,columnNameInExcel);
            }
        } catch (Exception e) {
            System.out.println("Error while reading the excel sheet.../n Please check if all column name are correct.");
            e.printStackTrace();
	        throw e;
        }
        return columnNameMappings;
    }
}
package xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

public class XLExport {

	private String[] kolom;
	private ArrayList<ArrayList<String>> baris = new ArrayList<ArrayList<String>>();
	private List<String> listKolom = new ArrayList<String>();
	private List<List<String>> listBaris = new ArrayList<List<String>>();
	HSSFWorkbook wb = new HSSFWorkbook();
	HSSFSheet sheet = null;
	private Map<String, String> prop;

	public XLExport() {
		super();
	}

	public XLExport(String[] kolom, ArrayList<ArrayList<String>> baris, String sheetName) {
		this.kolom = kolom;
		this.baris = baris;
		sheet = wb.createSheet(sheetName);
	}

	public XLExport(String[] kolom, ArrayList<ArrayList<String>> baris, Map<String, String> prop, String sheetName) {
		this.kolom = kolom;
		this.baris = baris;
		this.prop = prop;
		sheet = wb.createSheet(sheetName);
	}

	public XLExport(String sheetName) {
		sheet = wb.createSheet(sheetName);
	}

	public XLExport(List<String> kolom, List<List<String>> baris, Map<String, String> prop, String sheetName) {
		this.listKolom = kolom;
		this.listBaris = baris;
		this.prop = prop;
		sheet = wb.createSheet(sheetName);
	}

	public void proccessAndSaveTo(String sheetName, String location) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow row;

		Integer i = 0;
		int j = 0;
		row = sheet.createRow(i++);
		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		hCellStyle.setFont(font);
		int x = 0;
		for (String col : kolom) {
			row.createCell(j).setCellValue(col);
			row.setRowStyle(hCellStyle);
			sheet.autoSizeColumn(x++);
			j++;
		}

		for (ArrayList<String> bArr : baris) {
			row = sheet.createRow(i++);
			int r = 0;
			for (String b : bArr) {
				row.createCell(r).setCellValue(b);
				r++;
			}
		}

		FileOutputStream fileOut = new FileOutputStream(location);
		wb.write(fileOut);
		fileOut.close();
		System.out.println("DONE");
	}

	public InputStream toInputStream() throws IOException {
		HSSFRow row;

		Integer i = 0;
		int j = 0;

		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hCellStyle.setFont(font);

		if (prop != null) {
			for (String key : prop.keySet()) {
				j = 0;
				row = sheet.createRow(i++);
				Cell cell = row.createCell(j++);
				cell.setCellValue(key);
				cell = row.createCell(j++);
				cell.setCellValue(":");
				cell = row.createCell(j++);
				cell.setCellValue(prop.get(key));
				cell.setCellStyle(hCellStyle);

			}
		}
		row = sheet.createRow(i++);
		j = 0;
		for (String col : kolom) {
			System.err.println(col);
			Cell cell = row.createCell(j);
			cell.setCellValue(col);
			cell.setCellStyle(hCellStyle);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (baris != null && baris.size() > 0) {
			for (ArrayList<String> bArr : baris) {
				row = sheet.createRow(i++);
				int r = 0;
				for (String b : bArr) {
					row.createCell(r).setCellValue(b);
					r++;
				}
			}
		}
		File tempFile = File.createTempFile("tempFile", ".tmp");
		FileOutputStream fileOut = new FileOutputStream(tempFile);
		wb.write(fileOut);
		fileOut.close();
		InputStream inputStream = new FileInputStream(tempFile);
		tempFile.delete();
		return inputStream;
	}

	public void toFile(String location, String fileName) throws IOException {
		HSSFRow row;
		Integer i = 0;
		int j = 0;

		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hCellStyle.setFont(font);

		if (prop != null) {
			for (String key : prop.keySet()) {
				j = 0;
				row = sheet.createRow(i++);
				Cell cell = row.createCell(j++);
				cell.setCellValue(key);
				cell = row.createCell(j++);
				cell.setCellValue(":");
				cell = row.createCell(j++);
				cell.setCellValue(prop.get(key));
				cell.setCellStyle(hCellStyle);

			}
		}
		row = sheet.createRow(i++);
		j = 0;
		for (String col : kolom) {
			//System.err.println(col);
			Cell cell = row.createCell(j);
			cell.setCellValue(col);
			cell.setCellStyle(hCellStyle);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (baris != null && baris.size() > 0) {
			for (ArrayList<String> bArr : baris) {
				row = sheet.createRow(i++);
				int r = 0;
				for (String b : bArr) {
					row.createCell(r).setCellValue(b);
					r++;
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(location + "/" + fileName);
		wb.write(fileOut);
		fileOut.close();
	}

	public void toFile(String location, String fileName, String[] kolom, ArrayList<Map<String, Object>> barisMap)
			throws IOException {
		HSSFRow row;
		Integer i = 0;
		int j = 0;

		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hCellStyle.setFont(font);

		row = sheet.createRow(i++);
		j = 0;
		for (String col : kolom) {
		//	System.err.println(col);
			Cell cell = row.createCell(j);
			cell.setCellValue(col);
			cell.setCellStyle(hCellStyle);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (barisMap != null && barisMap.size() > 0) {
			for (Map<String, Object> bArr : barisMap) {
				row = sheet.createRow(i++);
				int r = 0;
				for (String s : bArr.keySet()) {
					System.out.println(s);
				}
				for (String b : kolom) {
					row.createCell(r).setCellValue((bArr.get(b) == null ? "" : bArr.get(b).toString()));
					r++;
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(location + "/" + fileName);
		wb.write(fileOut);
		fileOut.close();
	}

	public void toFile(String fileName, String[] kolom, ArrayList<Map<String, Object>> barisMap) throws IOException {
		HSSFRow row;
		Integer i = 0;
		int j = 0;

		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hCellStyle.setFont(font);

		row = sheet.createRow(i++);
		j = 0;
		for (String col : kolom) {
			// System.err.println(col);
			Cell cell = row.createCell(j);
			cell.setCellValue(col);
			cell.setCellStyle(hCellStyle);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (barisMap != null && barisMap.size() > 0) {
			for (Map<String, Object> bArr : barisMap) {
				row = sheet.createRow(i++);
				int r = 0;
				// for (String s : bArr.keySet()) {
				// System.out.println(s);
				// }
				for (String b : kolom) {
					row.createCell(r).setCellValue((bArr.get(b) == null ? "" : bArr.get(b).toString()));
					// System.out.println(bArr.get(b));
					r++;
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(fileName);
		wb.write(fileOut);
		fileOut.close();
	}

	public void toFile(String location, String fileName, List<String> kolom) throws IOException {
		HSSFRow row;
		Integer i = 0;
		int j = 0;

		CellStyle hCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		hCellStyle.setFont(font);

		if (prop != null) {
			for (String key : prop.keySet()) {
				j = 0;
				row = sheet.createRow(i++);
				Cell cell = row.createCell(j++);
				cell.setCellValue(key);
				cell = row.createCell(j++);
				cell.setCellValue(":");
				cell = row.createCell(j++);
				cell.setCellValue(prop.get(key));
				cell.setCellStyle(hCellStyle);

			}
		}
		row = sheet.createRow(i++);
		j = 0;
		for (String col : kolom) {
			System.err.println(col);
			Cell cell = row.createCell(j);
			cell.setCellValue(col);
			cell.setCellStyle(hCellStyle);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (baris != null && baris.size() > 0) {
			for (ArrayList<String> bArr : baris) {
				row = sheet.createRow(i++);
				int r = 0;
				for (String b : bArr) {
					row.createCell(r).setCellValue(b);
					r++;
				}
			}
		}
		FileOutputStream fileOut = new FileOutputStream(location + "/" + fileName);
		wb.write(fileOut);
		fileOut.close();
	}
}

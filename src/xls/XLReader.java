package xls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLReader {
	private String fieldString;

	public String getFieldString() {

		return fieldString;
	}

	public XLReader() {
		fieldString = "";
	}

	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}

	public static List<List<Object>> readAsList(String path, int startRow) {
		List<List<Object>> listData = new ArrayList<List<Object>>();

		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			System.out.println(simpleTypeOfFile);
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {

				Row row = rit.next();
				if (row.getRowNum() >= (startRow - 1)) {
					List<Object> list = new ArrayList<Object>();
					for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
						Cell cell = cit.next();

						/* if (cell != null) { */
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							list.add(cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								list.add(sdf.format(cell.getDateCellValue()));

							} else {
								NumberFormat nf = new DecimalFormat("#");
								list.add(nf.format(cell.getNumericCellValue()));
								list.add(cell.getNumericCellValue());
							}
							break;

						}
					}

					/* } */
					listData.add(list);
				}
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return listData;

	}

	public List<List<String>> readAsListString(String path, int startRow) {
		List<List<String>> listData = new ArrayList<List<String>>();

		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			System.out.println(simpleTypeOfFile);
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {

				Row row = rit.next();
				if (row.getRowNum() >= (startRow - 1)) {
					List<String> list = new ArrayList<String>();
					for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
						Cell cell = cit.next();

						/* if (cell != null) { */
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							list.add(cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								list.add(sdf.format(cell.getDateCellValue()));

							} else {
								NumberFormat nf = new DecimalFormat("#");
								list.add(nf.format(cell.getNumericCellValue()));

							}
							break;

						}
					}

					/* } */
					listData.add(list);
				}
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return listData;

	}

	public static List<List<Object>> readAsPlainList(String path, int limit) {
		List<List<Object>> listData = new ArrayList<List<Object>>();

		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			int ctr = 0;
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
				if (ctr < limit) {
					Row row = rit.next();
					List<Object> list = new ArrayList<Object>();
					for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
						Cell cell = cit.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							list.add(cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								list.add(sdf.format(cell.getDateCellValue()));

							} else {
								NumberFormat nf = new DecimalFormat("####");
								list.add(nf.format(cell.getNumericCellValue()));
								// list.add(cell.getNumericCellValue());
							}
							break;
						}
					}
					listData.add(list);
				} else {
					break;
				}
				ctr++;
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block

			e2.printStackTrace();
		}

		return listData;

	}

	public static List<List<Object>> readAsPlainList(String path) {
		List<List<Object>> listData = new ArrayList<List<Object>>();

		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
				Row row = rit.next();
				List<Object> list = new ArrayList<Object>();
				for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
					Cell cell = cit.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						list.add(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							list.add(sdf.format(cell.getDateCellValue()));

						} else {
							NumberFormat nf = new DecimalFormat("####");
							list.add(nf.format(cell.getNumericCellValue()));
							// list.add(cell.getNumericCellValue());
						}
						break;
					}
				}
				listData.add(list);

			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block

			e2.printStackTrace();
		}

		return listData;

	}

	public List<String> firstRowAsColumn(String path) {
		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;
		List<String> kolom = new ArrayList<String>();
		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);

			Row r = sheet.getRow(0);
			Integer count = 0;

			if (count == 0) {

				for (Iterator<Cell> cit = r.cellIterator(); cit.hasNext();) {
					Cell cell = cit.next();
					kolom.add(cell.getStringCellValue());
					// System.out.println(cell.getStringCellValue());
				}

			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return kolom;
	}

	public List<Map<String, Object>> readAsMap(String path) {
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);

			Row r = sheet.getRow(0);
			Integer count = 0;
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
				Row row = rit.next();
				if (count == 0) {

					for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {
						Cell cell = cit.next();
						fieldString += cell.getStringCellValue() + ",";
						// System.out.println(cell.getStringCellValue());
					}
					fieldString = fieldString.substring(0, fieldString.length() - 1);
				}
				if (count > 0) {
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					Integer ctr = 0;
					for (Iterator<Cell> cit = row.cellIterator(); cit.hasNext();) {

						Cell cell = cit.next();

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							map.put(r.getCell(ctr).toString(), cell.getStringCellValue());
							// System.out.println(cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								map.put(r.getCell(ctr).toString(), sdf.format(cell.getDateCellValue()));

							} else {
								NumberFormat nf = new DecimalFormat("#");
								map.put(r.getCell(ctr).toString(), nf.format(cell.getNumericCellValue()));
							}
							break;
						}
						ctr++;
					}
					listData.add(map);
				}
				count++;
			}
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return listData;

	}

	public static List<String> readColumnAsList(String path) {
		List<String> list = new ArrayList<String>();
		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			int counter = 0;
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
				counter++;
				if (counter <= 100) {
					Row row = rit.next();
					Cell cell = row.getCell(0);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						list.add(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							list.add(sdf.format(cell.getDateCellValue()));

						} else {
							NumberFormat nf = new DecimalFormat("#");
							list.add(nf.format(cell.getNumericCellValue()));
						}
						break;
					}
				} else {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return list;
	}

	public static List<String> readColumnAsListString(String path) {
		List<String> list = new ArrayList<String>();
		Workbook wb = null;
		Sheet sheet = null;
		InputStream file;

		try {
			file = new FileInputStream(path);
			String[] splitPath = path.split("\\.");
			String simpleTypeOfFile = splitPath[splitPath.length - 1];
			if (simpleTypeOfFile.equals("xlsx")) {
				try {
					wb = new XSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (simpleTypeOfFile.equals("xls")) {
				try {
					wb = new HSSFWorkbook(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sheet = wb.getSheetAt(0);
			int counter = 0;
			for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();) {
				counter++;
				if (counter <= 100) {
					Row row = rit.next();
					Cell cell = row.getCell(0);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						list.add(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							list.add(sdf.format(cell.getDateCellValue()));

						} else {
							NumberFormat nf = new DecimalFormat("#");
							list.add(nf.format(cell.getNumericCellValue()));
						}
						break;
					}
				} else {
					break;
				}
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return list;
	}
}

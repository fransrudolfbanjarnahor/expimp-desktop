package sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import helper.FieldAndType;

public class SQLOperation {

	private String table = "";
	private String field = "*";
	private String order = "";
	private String sorting = "";
	private String sqltable = "";
	private String selection = "";
	private Connection con = null;
	private ResultSet rs;
	private int totalpage;
	private long totalrow;
	private int page;
	private String debugsql = "";
	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public SQLOperation(Connection c) {
		this.con = c;
	}

	private String desimalFormat(Double nilai) {
		DecimalFormat df = new DecimalFormat("################");
		String n = df.format(nilai);
		return n;
	}

	public Timestamp string2Timestamp(String date) {
		DateFormat df = new SimpleDateFormat("yyy-MM-dd");
		java.util.Date dt = null;
		try {
			dt = df.parse(date);
		} catch (ParseException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		Timestamp sqlDate = new Timestamp(dt.getTime());
		return sqlDate;
	}

	public java.sql.Date string2Date(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dt = null;
		try {
			dt = df.parse(date);
		} catch (ParseException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.sql.Date sqlDate = new java.sql.Date(dt.getTime());
		return sqlDate;
	}

	public String date2String(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dt = null;
		try {
			dt = df.parse(date);
		} catch (ParseException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return dt.toString();
	}

	public String getDebugSQL() {
		return this.debugsql;
	}

	public String[] getFieldType() {
		String[] coltype = null;
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();
			coltype = new String[colcount];
			for (int i = 0; i < colcount; i++) {
				coltype[i] = rsmd.getColumnTypeName(i + 1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return coltype;
	}

	public boolean isDatabaseConnected() {
		try {
			if (this.con.isClosed()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}

	public String printFieldType() {
		String debugprint = "";
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();

			for (int i = 0; i < colcount; i++) {
				debugprint = debugprint + i + ":" + rsmd.getColumnTypeName(i + 1) + "<br />";
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return debugprint;
	}

	public String getDatabaseDriver() {
		String ret = "";
		try {
			DatabaseMetaData dbmeta = this.con.getMetaData();
			// ret = dbmeta.getDriverName() + " " + dbmeta.getDriverVersion() +
			// " " + dbmeta.getCatalogTerm() + " " + dbmeta;
			ret = dbmeta.getDriverName();
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return ret;
	}

	public String getDatabaseMeta() {
		String ret = "";
		try {
			DatabaseMetaData dbmeta = this.con.getMetaData();
			ret = dbmeta.getDriverName() + " | " + dbmeta.getDriverVersion() + " " + dbmeta.getCatalogTerm() + " | "
					+ dbmeta.getDatabaseProductName();
			// ret = dbmeta.getDriverName();
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return ret;
	}

	public String getDBMSName() {
		String ret = "";
		try {
			DatabaseMetaData dbmeta = this.con.getMetaData();
			ret = dbmeta.getDatabaseProductName().toUpperCase();
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return ret;
	}

	public void resetAutoIncrement() {
		System.out.println(getDBMSName());
		switch (DBType.valueOf(this.getDBMSName())) {
		case MYSQL:

			break;
		case POSTGRESQL:
			sqltable = "TRUNCATE " + table + " RESTART IDENTITY CASCADE";
			this.ExecuteSQL();
			break;
		case H2:
			sqltable = "ALTER " + table + " ALTER COLUMN ID RESTART WITH 1";
			this.ExecuteSQL();
			break;
		default:
			break;
		}
	}

	public void Execute(Integer type) {
		Statement qry;
		try {

			switch (type) {
			case 1:
				qry = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				break;

			default:
				qry = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				break;
			}
			/*
			 * qry = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			 * ResultSet.CONCUR_READ_ONLY);
			 */// qry.setQueryTimeout(120);
			qry.executeQuery(this.sqltable);

			this.rs = qry.getResultSet();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void Execute() {
		Statement qry;
		try {

			qry = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			qry.executeQuery(this.sqltable);

			this.rs = qry.getResultSet();
		} catch (SQLException ex) {
			error = ex.getMessage();
			// System.out.println(error);
			// ex.printStackTrace();
		}
	}

	public void ExecuteSQL() {
		Statement qry;
		try {
			qry = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			qry.executeQuery(this.sqltable);
			this.rs = qry.getResultSet();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public boolean executeSQL() {
		Statement qry;
		try {
			qry = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			qry.executeQuery(this.sqltable);
			this.rs = qry.getResultSet();
			return true;
		} catch (SQLException ex) {
			// ex.printStackTrace();
			error = ex.getMessage();
			return false;
		}

	}

	public void setTable(String t) {
		this.table = t;
		this.sqltable = ("select * from " + this.table + this.selection);
	}

	public void setTable(String t, String f) {
		this.table = t;
		this.field = f;
		this.sqltable = ("select " + f + " from " + this.table + this.selection);
		this.debugsql += this.sqltable + "<br >";
	}

	public void setTable(String t, String f, String o) {
		this.table = t;
		this.field = f;
		this.order = o;
		this.sqltable = ("select " + f + " from " + this.table + this.selection + " order by " + o + " asc");
		this.debugsql += this.sqltable + "<br >";
	}

	public boolean DeleteData() {
		boolean success = false;
		PreparedStatement pst = null;
		try {
			switch (DBType.valueOf(this.getDBMSName())) {
			case H2:
				pst = this.con.prepareStatement("delete from " + this.table + this.selection);

				break;
			default:
				pst = this.con.prepareStatement("delete from " + this.table + this.selection);

			}
			int s = pst.executeUpdate();
			this.sqltable = pst.toString();
			if (s > 0) {
				success = true;
			} else {
				success = false;
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return success;
	}

	public boolean UpdateData(Object[] data) {
		boolean success = false;
		try {
			String[] fld = getFieldName();
			String[] fldtype = getFieldType();
			String sql = "update " + this.table + " set ";

			for (int i = 0; i < fld.length; i++) {
				if (i < fld.length - 1) {
					sql = sql + fld[i] + "=?,";
				} else {
					sql = sql + fld[i] + "=? " + this.selection;
				}
			}

			PreparedStatement pst = this.con.prepareStatement(sql);

			for (int i = 0; i < fldtype.length; i++) {
				String t = fldtype[i].toLowerCase();

				if ((t.equals("char")) || (t.equals("varchar")) || (t.equals("varchar2")) || (t.equals("text"))
						|| (t.equals("nvarchar"))) {
					if (data[i] != null) {
						pst.setString(i + 1, data[i].toString());
					} else {
						pst.setString(i + 1, "");
					}
				}

				if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
					if (data[i] != null) {
						pst.setDate(i + 1, string2Date(data[i].toString()));
					} else {
						pst.setString(i + 1, null);
					}
				}

				if ((t.equals("int")) || (t.equals("smallint"))) {
					if (data[i] != null) {
						pst.setInt(i + 1, Integer.valueOf(data[i].toString()).intValue());
					} else {
						pst.setNull(i + 1, 0);
					}
				}

				if (t.equals("long")) {
					if (data[i] != null) {
						pst.setLong(i + 1, Long.valueOf(data[i].toString()).longValue());
					} else {
						pst.setNull(i + 1, 0);
					}
				}

				if ((t.equals("decimal")) || (t.equals("money"))) {
					if (data[i] != null) {
						pst.setDouble(i + 1, Double.valueOf(data[i].toString()).doubleValue());
					} else {
						pst.setNull(i + 1, 0);
					}
				}
			}

			int s = pst.executeUpdate();

			if (s > 0) {
				success = true;
			} else {
				success = false;
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return success;
	}

	public void TablePaging(String tablename, String colmodel, String sidx, String sord, int l, int p) {
		if (this.getDatabaseDriver().indexOf("Oracle") > 0) {
			OracleTablePaging(tablename, colmodel, sidx, sord, l, p);
		} else if (this.getDatabaseDriver().indexOf("SQL Server") > 0) {
			SQLServerTablePaging(tablename, colmodel, sidx, sord, l, p);
		} else {
			PostgreTablePaging(tablename, colmodel, sidx, sord, l, p);
		}

	}

	public boolean SQLUpdate(Map data) {
		boolean success = false;
		try {
			Map fieldmeta = getFieldNameAndType();
			Set keys = data.keySet();

			String sql = "update " + this.table + " set ";

			for (Iterator i = keys.iterator(); i.hasNext();) {
				String key = i.next().toString();
				sql = sql + key + "=?,";
			}

			sql = sql.substring(0, sql.length() - 1) + this.selection;
			this.sqltable = (sql + data.toString());
			this.debugsql += this.sqltable;
			PreparedStatement pst = this.con.prepareStatement(sql);

			int k = 0;
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String f = (String) i.next();
				k++;
				String t = fieldmeta.get(f).toString().toLowerCase();

				if ((t.equals("char")) || (t.equals("varchar")) || (t.equals("varchar2")) || (t.equals("text"))
						|| (t.equals("nvarchar"))) {
					pst.setString(k, data.get(f).toString());
				}

				if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
					pst.setDate(k, string2Date(data.get(f).toString()));
				}

				if (t.equals("timestamp")) {
					pst.setTimestamp(k, string2Timestamp(data.get(f).toString()));
				}

				if ((t.equals("int")) || (t.equals("smallint")) || (t.equals("int2") || (t.equals("int4")))) {
					pst.setInt(k, Integer.valueOf(data.get(f).toString()).intValue());
				}
				if (t.equals("long")) {
					pst.setLong(k, Long.valueOf(data.get(f).toString()).longValue());
				}
				if ((t.equals("decimal")) || (t.equals("money")) || (t.equals("float")) || (t.equals("number"))
						|| (t.equals("numeric"))) {
					pst.setDouble(k, Double.valueOf(data.get(f).toString()).doubleValue());
				}
			}
			this.sqltable = pst.toString();
			int s = pst.executeUpdate();

			if (s > 0) {
				success = true;
			} else {
				success = false;
			}
		} catch (Exception ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return success;
	}

	public String[] getFieldName() {
		String[] colname = null;
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();
			colname = new String[colcount];
			for (int i = 0; i < colcount; i++) {
				colname[i] = rsmd.getColumnName(i + 1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return colname;
	}

	// for mysql 5.1up
	public String[] getFieldLabel() {
		String[] colname = null;
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();
			colname = new String[colcount];
			for (int i = 0; i < colcount; i++) {
				colname[i] = rsmd.getColumnLabel(i + 1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return colname;
	}

	public ArrayList<String> getArrayFieldName() {
		ArrayList<String> colname = new ArrayList<String>();
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();
			for (int i = 0; i < colcount; i++) {
				colname.add(rsmd.getColumnName(i + 1));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return colname;
	}

	public Map<String, String> getFieldNameAndType() {
		Map<String, String> meta = new LinkedHashMap<String, String>();
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();

			for (int i = 0; i < colcount; i++) {
				meta.put(rsmd.getColumnName(i + 1), rsmd.getColumnTypeName(i + 1).toLowerCase());
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return meta;
	}

	public List<FieldAndType> getListFieldNameAndType() {
		List<FieldAndType> meta = new ArrayList<>();
		try {
			ResultSetMetaData rsmd = this.rs.getMetaData();
			int colcount = rsmd.getColumnCount();

			for (int i = 0; i < colcount; i++) {
				meta.add(new FieldAndType(rsmd.getColumnName(i + 1), rsmd.getColumnTypeName(i + 1).toLowerCase()));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return meta;
	}

	public boolean InsertData(Object[] data) {
		boolean success = false;
		try {
			String[] fld = getFieldName();
			String[] fldtype = getFieldType();
			String sql = "Insert into " + this.table + "( ";
			String sqlval = " values(";
			for (int i = 0; i < fld.length; i++) {
				if (i < fld.length - 1) {
					sql = sql + fld[i] + ",";
					sqlval = sqlval + "?,";
				} else {
					sql = sql + fld[i] + ")";
					sqlval = sqlval + "?)";
				}

			}

			PreparedStatement pst = this.con.prepareStatement(sql + sqlval);

			for (int i = 0; i < fldtype.length; i++) {
				String t = fldtype[i].toLowerCase();

				if ((t.equals("char")) || (t.equals("varchar2")) || (t.equals("varchar")) || (t.equals("text"))
						|| (t.equals("nvarchar"))) {
					pst.setString(i + 1, data[i].toString());
				}
				if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
					pst.setDate(i + 1, string2Date(data[i].toString()));
				}
				if ((t.equals("int")) || (t.equals("smallint"))) {
					pst.setInt(i + 1, Integer.valueOf(data[i].toString()).intValue());
				}
				if (t.equals("long")) {
					pst.setLong(i + 1, Long.valueOf(data[i].toString()).longValue());
				}
				if ((t.equals("decimal")) || (t.equals("money")) || (t.equals("float"))) {
					pst.setDouble(i + 1, Double.valueOf(data[i].toString()).doubleValue());
				}

			}

			int s = pst.executeUpdate();
			this.sqltable = pst.toString();
			if (s > 0) {
				success = true;
			} else {
				success = false;
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return success;
	}

	public Integer getMaxField(String fld) {
		Integer MAX = 1;
		try {
			rs.first();
			if (rs.getInt(fld) != 0) {
				MAX = rs.getInt(fld) + 1;
			}
			// MAX =StringUtils.leftPad(str, 10)

		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return MAX;
	}

	public boolean SQLInsert(Map<String, Object> data) {
		boolean success = true;
		try {
			String sql = "insert into " + this.table + "( ";
			String sqlval = " values(";
			Map<String, String> fieldmeta = getFieldNameAndType();
			for (String key : data.keySet()) {
				sql = sql + key + ",";
				sqlval = sqlval + "?,";
			}

			sql = sql.substring(0, sql.length() - 1) + ")";
			sqlval = sqlval.substring(0, sqlval.length() - 1) + ")";
			this.sqltable = (sql + sqlval);
			PreparedStatement pst = this.con.prepareStatement(this.sqltable);
			this.debugsql += " " + this.sqltable;

			int k = 0;
			for (String key : data.keySet()) {
				String f = key;
				k++;
				String t = fieldmeta.get(f).toString().toLowerCase();
				if ((t.equals("char")) || (t.equals("varchar2")) || (t.equals("varchar")) || (t.equals("text"))
						|| (t.equals("nvarchar"))) {
					pst.setString(k, data.get(f).toString());
				}
				if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
					pst.setDate(k, string2Date(data.get(f).toString()));
				}
				if (t.equals("timestamp")) {
					pst.setTimestamp(k, string2Timestamp(data.get(f).toString()));
				}
				if (t.equals("int") || t.equals("smallint") || t.equals("int2") || t.equals("int4")) {
					pst.setInt(k, Integer.valueOf(data.get(f).toString()).intValue());
				}
				if (t.equals("long")) {
					pst.setLong(k, Long.valueOf(data.get(f).toString()).longValue());
				}
				if ((t.equals("decimal")) || (t.equals("money")) || (t.equals("float")) || (t.equals("number"))
						|| (t.equals("numeric"))) {
					if (!data.get(f).equals("")) {
						pst.setDouble(k, Double.valueOf(data.get(f).toString()).doubleValue());
					} else {
						pst.setDouble(k, 0.0D);
					}
				}

			}
			boolean s = pst.execute();
			this.sqltable = pst.toString();

			this.debugsql += " " + this.sqltable;
			// System.out.println(this.sqltable);
			if (s) {
				success = true;
			} else {
				success = false;
			}
		} catch (Exception ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return success;
	}

	public boolean SQLInsertList(List<Map<String, Object>> data) {
		boolean success = true;
		try {
			String sql = "insert into " + this.table + "(";
			String sqlval = " values(";
			Map<String, String> fieldmeta = getFieldNameAndType();

			for (String key : fieldmeta.keySet()) {
				sql = sql + key + ",";
				sqlval = sqlval + "?,";
			}

			sql = sql.substring(0, sql.length() - 1) + ")";
			sqlval = sqlval.substring(0, sqlval.length() - 1) + ")";

			this.sqltable = (sql + sqlval);

			PreparedStatement pst = this.con.prepareStatement(this.sqltable);

			this.debugsql += " " + this.sqltable;
			// System.out.println(fieldmeta);
			Set<String> temp = fieldmeta.keySet();
			boolean checkField = false;
			boolean checkError = false;
			for (Map<String, Object> row : data) {
				// System.out.println(row.keySet());
				if (!checkField) {
					for (String f : row.keySet()) {
						if (fieldmeta.get(f.toLowerCase()) == null) {
							checkField = true;
							checkError = true;
							break;
						}
					}
				}
				if (!checkError) {
					int k = 1;
					for (String fieldName : row.keySet()) {
						String f = fieldName;

						/* if (!f.toLowerCase().equals("id")) { */

						String t = fieldmeta.get(f.toLowerCase());
						/*
						 * System.out.println(f.toLowerCase() + " " + t + " " +
						 * " " + row.get(f).toString() + " " + k);
						 */
						if ((t.equals("char")) || (t.equals("varchar2")) || (t.equals("varchar")) || (t.equals("text"))
								|| (t.equals("nvarchar"))) {
							if (row.get(f) != null) {
								pst.setString(k, row.get(f).toString().replaceAll("'", "''"));
							} else {
								pst.setString(k, null);
							}
						}
						// pst.set
						if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
							if (row.get(f) != null) {
								pst.setDate(k, string2Date(row.get(f).toString()));
							} else {
								pst.setDate(k, null);
							}
						}
						if (t.equals("timestamp")) {
							if (row.get(f) != null) {
								pst.setTimestamp(k, string2Timestamp(row.get(f).toString()));
							} else {
								pst.setDate(k, null);
							}
						}
						if (t.equals("int") || t.equals("smallint") || t.equals("int2") || t.equals("int4")) {
							if (row.get(f) != null) {
								pst.setInt(k, Integer.valueOf(row.get(f).toString()).intValue());
							} else {
								pst.setObject(k, null);
							}
						}
						if (t.equals("long")) {

							if (row.get(f) != null) {
								pst.setLong(k, Long.valueOf(row.get(f).toString()).longValue());
							} else {
								pst.setObject(k, null);
							}
						}
						if ((t.equals("decimal")) || (t.equals("money")) || (t.equals("float")) || (t.equals("number"))
								|| (t.equals("numeric"))) {
							if (row.get(f) != null) {
								if (!row.get(f).equals("")) {
									pst.setDouble(k, Double.valueOf(row.get(f).toString()).doubleValue());
								} else {
									pst.setDouble(k, 0.0D);
								}
							} else {
								pst.setObject(k, null);
							}
						}
						k++;
						/*
						 * } else { pst.setNull(k, Types.INTEGER); }
						 */

					}
					pst.addBatch();
				} else {
					break;
				}
			}

			int[] s = pst.executeBatch();
			this.sqltable = pst.toString();
			this.debugsql += " " + this.sqltable;
			// System.out.println(debugsql);
			if (s.length > 0) {
				success = true;
			} else {
				success = false;
			}

		} catch (Exception ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return success;
	}

	public boolean SQLInsertList(List<Map<String, Object>> data, String fieldOrder) {
		boolean success = true;
		try {
			String sql = "insert into " + this.table + "(";
			sql += fieldOrder + ")";
			String sqlval = " values(";
			Map<String, String> fieldmeta = getFieldNameAndType();

			for (String key : fieldmeta.keySet()) {
				// sql = sql + key + ",";
				sqlval = sqlval + "?,";
			}

			sql = sql.substring(0, sql.length() - 1) + ")";
			sqlval = sqlval.substring(0, sqlval.length() - 1) + ")";

			this.sqltable = (sql + sqlval);
			// System.out.println(sqltable);
			PreparedStatement pst = this.con.prepareStatement(this.sqltable);

			this.debugsql += " " + this.sqltable;
			// System.out.println(fieldmeta);
			Set<String> temp = fieldmeta.keySet();
			boolean checkField = false;
			boolean checkError = false;
			// System.out.println(data.size());
			for (Map<String, Object> row : data) {
				System.out.println(row.keySet() + " " + fieldmeta);
				if (!checkField) {
					for (String f : row.keySet()) {
						if (fieldmeta.get(f.toLowerCase()) == null) {
							checkField = true;
							checkError = true;
							break;
						}
					}
				}
				// System.out.println(row.keySet() + " " + checkError);
				if (!checkError) {
					int k = 1;
					for (String fieldName : fieldOrder.split(",")) {
						String f = fieldName;

						/* if (!f.toLowerCase().equals("id")) { */

						String t = fieldmeta.get(f.toLowerCase());

						// System.out.println(f.toLowerCase() + " " + t + " " +
						// " " + row.get(f).toString() + " " + k);

						if (t.equals("serial")) {
							if (row.get(f) != null) {
								pst.setInt(k, Integer.valueOf(row.get(f).toString()).intValue());
							} else {
								pst.setInt(k, 0);
							}
						}

						if ((t.equals("char")) || (t.equals("varchar2")) || (t.equals("varchar")) || (t.equals("text"))
								|| (t.equals("nvarchar") || (t.equals("longtext")))) {
							if (row.get(f) != null) {
								pst.setString(k, row.get(f).toString());
							} else {
								pst.setString(k, null);
							}

						}
						if (t.equals("boolean") || t.equals("bool")) {

							if (row.get(f) != null) {
								pst.setBoolean(k, (boolean) row.get(f));
							} else {
								pst.setBoolean(k, false);
							}
						}
						// pst.set
						if ((t.equals("date")) || (t.equals("datetime")) || (t.equals("smalldatetime"))) {
							if (row.get(f) != null) {
								pst.setDate(k, string2Date(row.get(f).toString()));
							} else {
								pst.setDate(k, null);
							}
						}

						if (t.equals("timestamp")) {
							if (row.get(f) != null) {
								pst.setDate(k, string2Date(row.get(f).toString()));
							} else {
								pst.setDate(k, null);
							}
						}

						if (t.equals("integer") || t.equals("int") || t.equals("smallint") || t.equals("int2")
								|| t.equals("int4") || t.equals("bigint") || t.equals("bigint unsigned")) {
							if (row.get(f) != null) {
								pst.setInt(k, Integer.valueOf(row.get(f).toString()).intValue());
							} else {
								pst.setInt(k, 0);
							}
						}

						if (t.equals("long") || t.equals("int8")) {

							if (row.get(f) != null) {
								pst.setLong(k, Long.valueOf(row.get(f).toString()).longValue());
							} else {
								pst.setLong(k, 0L);
							}
						}

						if ((t.equals("decimal")) || (t.equals("money")) || (t.equals("float")) || (t.equals("number"))
								|| (t.equals("numeric") || (t.equals("double")))) {
							if (row.get(f) != null) {
								if (!row.get(f).equals("")) {
									pst.setDouble(k, Double.valueOf(row.get(f).toString()).doubleValue());
								} else {
									pst.setDouble(k, 0.0D);
								}
							} else {
								pst.setDouble(k, 0.0);
							}
						}

						if (t.equals("bytea")) {
							if (row.get(f) != null) {
								pst.setBytes(k, (byte[]) row.get(f));
							} else {
								pst.setBytes(k, null);
							}
						}
						k++;
						/*
						 * } else { pst.setNull(k, Types.INTEGER); }
						 */

					}
					pst.addBatch();
				} else {
					break;
				}
			}
			int[] s = pst.executeBatch();
			// this.sqltable = pst.toString();
			this.debugsql += " " + this.sqltable;
			// System.out.println(debugsql);
			if (s.length > 0) {
				success = true;
			} else {
				success = false;
			}

		} catch (Exception ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.getMessage());
		}

		return success;
	}

	public void setSelection(String s) {
		this.selection = (" where " + s + " ");
	}

	public void addSelection(String fld, Object val) {
		this.selection = " WHERE ";
		if (val.getClass().equals(Integer.TYPE)) {
			this.selection += fld + "=" + val.toString();
		} else {
			this.selection += fld + "='" + val.toString() + "'";
		}
	}

	public void likeSelection(String likesql) {

		this.selection += likesql;
	}

	public void andSelection(String fld, Object val) {
		if (val.getClass().equals(Integer.TYPE)) {
			this.selection += " AND " + fld + "=" + val.toString();
		} else {
			this.selection += " AND " + fld + "='" + val.toString() + "'";
		}

	}

	public void orSelection(String fld, Object val) {
		if (val.getClass().equals(Integer.TYPE)) {
			this.selection += " AND " + fld + "=" + val.toString();
		} else {
			this.selection += " AND " + fld + "='" + val.toString() + "'";
		}

	}

	public void setTable(String t, String f, String o, String s) {
		this.table = t;
		this.field = f;
		this.order = o;
		this.sorting = s;
		this.sqltable = ("select " + f + " from " + this.table + this.selection + " order by " + o + " " + s);
	}

	public void setTable(String t, String f, String o, String s, String limit, String offset) {
		this.table = t;
		this.field = f;
		this.order = o;
		this.sorting = s;
		this.sqltable = ("select " + f + " from " + this.table + this.selection + " order by " + o + " " + s + " limit "
				+ limit + " offset " + offset);
	}

	public void setSql(String sqltext) {
		this.sqltable = sqltext;
	}

	public void SQLServerTablePaging(String t, String f, String o, String s, int l, int p) {
		this.table = t;
		this.totalrow = getTotalRowTable();

		this.field = f;
		this.order = o;
		this.sorting = s;
		int limit = l;
		if (this.totalrow > 0L) {
			totalpage = (int) Math.ceil((double) totalrow / (double) limit);
		} else {
			this.totalpage = 0;
		}

		this.page = p;

		if (this.page > this.totalpage) {
			this.page = this.totalpage;
		}

		int limit_start = this.page == 1 ? 1 : limit * (this.page - 1);

		int limit_end = limit_start + limit;

		this.sqltable = ("SELECT " + this.field + " from " + " ( SELECT *, ROW_NUMBER() OVER (ORDER BY " + this.order
				+ " " + this.sorting + ") AS rownumber from " + this.table + " " + this.selection
				+ ") tbl where rownumber >= " + limit_start + " and rownumber < " + limit_end);
		this.debugsql += this.sqltable;
		Execute();
	}

	public void OracleTablePaging(String t, String f, String o, String s, Integer l, Integer p) {
		table = t;
		totalrow = this.getTotalRowTable();

		field = f;
		order = o;
		sorting = s;
		int limit = l;
		if (totalrow > 0) {
			totalpage = (int) Math.ceil((double) totalrow / (double) limit);
		} else {
			totalpage = 0;
		}
		// out.print(totalpage);
		page = p;

		if (page > totalpage) {
			page = totalpage;
		}

		int limit_start = page == 1 ? 1 : 1 + (limit * (page - 1));

		int limit_end = limit_start + limit;

		sqltable = "select " + field + " from (select rownum r, tbl.* from (select " + field + " from " + table + " "
				+ selection + " order by " + order + " " + sorting + ") tbl) where r >= " + limit_start + " and r < "
				+ limit_end;
		this.debugsql += this.sqltable + "<br >";
		// sqltable = "SELECT " + field + " from " +
		// " ( SELECT *, rownum r, OVER (ORDER BY " + order + " " + sorting +
		// ") AS rownumber from " + table + " " + selection +
		// ") tbl where rownumber >= " + limit_start + " and rownumber < " +
		// limit_end;

		// sqltable += String.valueOf(totalrow) + " " + totalpage + " " + page +
		// " " + limit_start;

		this.Execute();
	}

	public void PostgreTablePaging(String t, String f, String o, String s, Integer l, Integer p) {
		table = t;
		totalrow = this.getTotalRowTable();

		field = f;
		order = o;
		sorting = s;
		int limit = l;
		if (totalrow > 0) {
			totalpage = (int) Math.ceil((double) totalrow / (double) limit);
		} else {
			totalpage = 0;
		}
		// out.print(totalpage);
		page = p;

		if (page > totalpage) {
			page = totalpage;
		}

		int limit_start = page == 1 ? 0 : (limit * (page - 1));

		int limit_end = limit;

		sqltable = "select " + field + " from " + table + " " + selection + " order by " + order + " " + sorting
				+ " offset " + limit_start + " limit " + limit_end;
		this.debugsql += this.sqltable + "<br >";
		// sqltable = "SELECT " + field + " from " +
		// " ( SELECT *, rownum r, OVER (ORDER BY " + order + " " + sorting +
		// ") AS rownumber from " + table + " " + selection +
		// ") tbl where rownumber >= " + limit_start + " and rownumber < " +
		// limit_end;

		// sqltable += String.valueOf(totalrow) + " " + totalpage + " " + page +
		// " " + limit_start;

		this.Execute();
	}

	public long getTotalRow() {
		long tr = 0L;
		if (this.rs != null) {
			try {
				this.rs.last();
				tr = this.rs.getRow();
			} catch (SQLException ex) {
				tr = 0L;
			}
		}
		return tr;
	}

	public long getTotalRowTable() {
		long totalrow = 0L;
		setTable(this.table, " count(*) as JUMLAH ");
		Execute();
		try {
			this.rs.first();
			totalrow = this.rs.getLong("JUMLAH");
			this.debugsql += "Total Row = " + this.rs.getString("JUMLAH") + "<br />";
			// this.rs.close();
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}

		return totalrow;
	}

	public ResultSet getResultset() {
		return this.rs;
	}

	public String getSql() {
		return this.sqltable;
	}

	public Map getRow() {
		Map row = new HashMap();
		try {
			this.rs.first();
			for (String f : getFieldName()) {
				row.put(f.toUpperCase(), this.rs.getString(f));
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return row;
	}

	public ArrayList<ArrayList<String>> getArrayRow() {
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

		try {
			this.rs.beforeFirst();
			while (this.rs.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (String f : getArrayFieldName()) {
					row.add(this.rs.getString(f));
				}
				rows.add(row);
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rows;
	}

	public ArrayList<Map<String, Object>> getListMapRow() {
		ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		try {
			this.rs.beforeFirst();
			while (this.rs.next()) {
				Map<String, Object> row = new HashMap<>();
				for (String f : getArrayFieldName()) {
					row.put(f.toLowerCase(), this.rs.getString(f));
				}

				rows.add(row);
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rows;
	}

	public ArrayList<LinkedHashMap<String, String>> getListMapRowString() {
		ArrayList<LinkedHashMap<String, String>> rows = new ArrayList<LinkedHashMap<String, String>>();
		Map<String, String> fields = getFieldNameAndType();
		System.out.println(fields.toString());
		try {
			this.rs.beforeFirst();
			while (this.rs.next()) {
				LinkedHashMap<String, String> row = new LinkedHashMap<>();
				for (String f : getArrayFieldName()) {
					if (!fields.get(f).equals("bytea") && !fields.get(f).equals("image")) {
						row.put(f.toLowerCase(), (this.rs.getString(f) == null ? "null" : this.rs.getString(f)));
					} else {
						row.put(f.toLowerCase(), "image");
					}
				}
				rows.add(row);
				row = null;
			}
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rows;
	}

	public Map getRow2Map(String fieldkey, String fieldval) {
		Map row = new HashMap();
		try {
			this.rs.beforeFirst();
			while (this.rs.next()) {
				row.put(rs.getString(fieldkey), rs.getString(fieldval));
			}

		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return row;
	}

	public String xml4JQGrid(String id) {
		String strxml = "";
		try {
			this.rs.beforeFirst();

			strxml = strxml + "<rows>";
			strxml = strxml + "<page>" + this.page + "</page>";
			strxml = strxml + "<total>" + this.totalpage + "</total>";
			strxml = strxml + "<records>" + this.totalrow + "</records>";
			String[] fld = getFieldName();
			String[] ftype = getFieldType();
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			while (this.rs.next()) {
				strxml = strxml + "<row id='" + this.rs.getString(id) + "'>";
				for (int i = 0; i < fld.length; i++) {
					if ((ftype[i].equals("date")) || (ftype[i].equals("datetime"))
							|| (ftype[i].equals("smalldatetime"))) {
						strxml = strxml + "<cell><![CDATA[" + this.rs.getDate(fld[i]) + "]]></cell>";
					} else if (this.rs.getString(fld[i]) == null) {
						strxml = strxml + "<cell><![CDATA[]]></cell>";
					} else {
						strxml = strxml + "<cell><![CDATA[" + this.rs.getString(fld[i]) + "]]></cell>";
					}
				}
				strxml = strxml + "</row>";
			}
			strxml = strxml + "</rows>";
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return strxml;
	}

	public String json4JQGrid(String id) {
		String strxml = "";
		try {
			this.rs.beforeFirst();

			strxml = strxml + "<rows>";
			strxml = strxml + "<page>" + this.page + "</page>";
			strxml = strxml + "<total>" + this.totalpage + "</total>";
			strxml = strxml + "<records>" + this.totalrow + "</records>";
			String[] fld = getFieldName();
			String[] ftype = getFieldType();
			TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
			while (this.rs.next()) {
				strxml = strxml + "<row id='" + this.rs.getString(id) + "'>";
				for (int i = 0; i < fld.length; i++) {
					if ((ftype[i].equals("date")) || (ftype[i].equals("datetime"))
							|| (ftype[i].equals("smalldatetime"))) {
						strxml = strxml + "<cell><![CDATA[" + this.rs.getDate(fld[i]) + "]]></cell>";
					} else if (this.rs.getString(fld[i]) == null) {
						strxml = strxml + "<cell><![CDATA[]]></cell>";
					} else {
						strxml = strxml + "<cell><![CDATA[" + this.rs.getString(fld[i]) + "]]></cell>";
					}
				}
				strxml = strxml + "</row>";
			}
			strxml = strxml + "</rows>";
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return strxml;
	}

	public String Debug4JQGrid(String id) {
		String strxml = "";
		try {
			this.rs.beforeFirst();

			strxml = strxml + "<html><body>";
			strxml = strxml + "page :" + this.page + "<br />";
			strxml = strxml + "total" + this.totalpage + "<br />";
			strxml = strxml + "records" + this.totalrow + "<br />";
			String[] fld = getFieldName();
			String[] ftype = getFieldType();
			while (this.rs.next()) {
				strxml = strxml + "row id='" + this.rs.getString(id) + "<br />";
				for (int i = 0; i < fld.length; i++) {
					if (ftype[i].equals("date")) {
						strxml = strxml + "cell " + this.rs.getString(fld[i]) + "<br />";
					}
				}

			}

			strxml = strxml + "</body></html>";
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return strxml;
	}
}
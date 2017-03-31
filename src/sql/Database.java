package sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

	private Connection actCon = null;

	protected DBType dbms;
	protected String host_port = "";
	protected String db_host = "";
	protected String db_name = "";
	protected String db_user = "";
	protected String db_pass = "";
	protected String error;

	public Database(DBType dbType, String host, String port, String dbname, String dbuser, String dbpass) {
		this.dbms = dbType;
		this.host_port = port;
		this.db_host = host;
		this.db_name = dbname;
		this.db_user = dbuser;
		this.db_pass = dbpass;
	}

	public Database(DBConfig conf) {
		this.dbms = conf.getDbms();
		this.db_host = conf.getHost();
		this.host_port = conf.getPort();
		this.db_name = conf.getDatabase();
		this.db_user = conf.getUser();
		this.db_pass = conf.getPass();
	}

	public Connection openConnection() {
		String url;
		try {
			switch (dbms) {
			case MYSQL:
				Class.forName("com.mysql.jdbc.Driver");
				actCon = DriverManager.getConnection(
						"jdbc:mysql://" + this.db_host + ":" + host_port + "/" + this.db_name, this.db_user,
						this.db_pass);
				break;
			case POSTGRESQL:
				Class.forName("org.postgresql.Driver");
				url = "jdbc:postgresql://" + this.db_host + ":" + host_port + "/" + this.db_name;
				actCon = DriverManager.getConnection(url, this.db_user, this.db_pass);
				break;
			case SQLSERVER:
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				url = "jdbc:jtds:sqlserver://" + this.db_host + ":" + host_port + "/" + this.db_name;
				String id = this.db_user;
				String pass = this.db_pass;
				actCon = java.sql.DriverManager.getConnection(url, id, pass);
				break;
			case ORACLE:
				Class.forName("oracle.jdbc.OracleDriver");
				url = "jdbc:oracle:thin:" + this.db_name + "/" + this.db_pass + "@//" + this.db_host + ":" + host_port
						+ "/xe";
				actCon = java.sql.DriverManager.getConnection(url);
				break;
			case H2:
				Class.forName("org.h2.Driver");
				actCon = DriverManager.getConnection("jdbc:h2://" + this.db_host, this.db_user, this.db_pass);
				break;
			default:
				actCon = null;
				break;
			}

		} catch (SQLException | ClassNotFoundException ex) {
			setError("Driver " + ex.getMessage() + " Tidak Ditemukan");
			return null;
		}
		return actCon;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void closeConnection() {
		try {
			actCon.close();
		} catch (SQLException ex) {
			Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public Map<String, String> getDatabaseMeta() {
		Map<String, String> dbm = new HashMap<String, String>();
		try {
			DatabaseMetaData dbmeta = actCon.getMetaData();
			dbm.put("Driver Name", dbmeta.getDriverName());
			dbm.put("Driver Version", dbmeta.getDriverVersion() + " " + dbmeta.getDriverMajorVersion() + " "
					+ dbmeta.getDriverMinorVersion());
			dbm.put("Database Product", dbmeta.getDatabaseProductName() + " " + dbmeta.getDatabaseProductVersion());
		} catch (SQLException ex) {
			Logger.getLogger(SQLOperation.class.getName()).log(Level.SEVERE, null, ex);
		}
		return dbm;
	}

	public Map<String, String> getDatabaseTable() {
		Map<String, String> list = new HashMap<String, String>();
		String[] tableTypes = { "TABLE" };
		DatabaseMetaData dbmeta = null;
		try {
			if (actCon.getMetaData() != null) {
				dbmeta = actCon.getMetaData();
				// ResultSet rs = dbmeta.getTables("", schema, "%", tableTypes);
				ResultSet rs = dbmeta.getTables(null, null, null, tableTypes);
				while (rs.next()) {
					System.out.println(rs.getString("TABLE_NAME"));
					// System.out.println(rs.getString(2));
					if (rs.getString(2) != null) {
						list.put(rs.getString(2) + "/" + rs.getString("TABLE_NAME"),
								rs.getString(2) + "." + rs.getString("TABLE_NAME"));
					} else {
						list.put(rs.getString("TABLE_NAME"), rs.getString("TABLE_NAME"));
					}

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getAllTable() {
		List<String> list = new ArrayList<String>();
		String[] tableTypes = { "TABLE", "VIEW", "ALIAS", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM" };
		DatabaseMetaData dbmeta;
		try {
			dbmeta = actCon.getMetaData();
			ResultSet rs = dbmeta.getTables(null, null, null, tableTypes);
			while (rs.next()) {
				if (rs.getString(2) != null) {
					// if (rs.getString(2).equals("public") ||
					// rs.getString(2).equals("dbo")) {
					// list.add(rs.getString("TABLE_NAME"));
					// } else {
					list.add(rs.getString(2) + "." + rs.getString("TABLE_NAME"));
					// }
				} else {
					list.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getStatusConnection() {
		return actCon.toString();
	}
}

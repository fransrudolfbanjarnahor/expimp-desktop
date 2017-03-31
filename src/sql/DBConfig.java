package sql;

public class DBConfig {
	protected DBType dbms;
	protected String host = "";
	protected String port = "";
	protected String database = "";
	protected String user = "";
	protected String pass = "";

	public DBType getDbms() {
		return dbms;
	}

	public void setDbms(DBType dbms) {
		this.dbms = dbms;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public DBConfig(DBType dbms, String host, String port, String database,
			String user, String pass) {
		super();
		this.dbms = dbms;
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.pass = pass;
	}

	public DBConfig() {
		super();
	}

	@Override
	public String toString() {
		return "DBConfig [dbms=" + dbms + ", host=" + host + ", port=" + port
				+ ", database=" + database + ", user=" + user + ", pass="
				+ pass + "]";
	}

	

}

package sql;

public enum DBType {
	POSTGRESQL, MYSQL, SQLSERVER, ORACLE, H2;
	public static String[] names() {
		DBType[] states = values();
		String[] names = new String[states.length];

		for (int i = 0; i < states.length; i++) {
			names[i] = states[i].name();
		}
		return names;
	}
}

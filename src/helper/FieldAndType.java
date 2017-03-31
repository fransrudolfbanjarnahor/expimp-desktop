package helper;

public class FieldAndType {
	private String field;
	private String type;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FieldAndType(String field, String type) {
		super();
		this.field = field;
		this.type = type;
	}

}

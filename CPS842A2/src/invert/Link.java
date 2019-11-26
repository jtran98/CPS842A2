package invert;

public class Link {
	private String value;
	private String connection;
	public Link(String value, String connection) {
		this.value = value;
		this.connection = connection;
	}
	public Link () {
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getConnection() {
		return this.connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
}

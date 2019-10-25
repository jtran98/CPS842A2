//Jacky Tran 500766582
package eval;

public class Query {
	private String id = "";
	private String query = "";
	public Query(String id, String query) {
		this.id = id;
		this.query = query;
	}
	public Query() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String[] toArray() {
		return this.query.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s+");
	}
}

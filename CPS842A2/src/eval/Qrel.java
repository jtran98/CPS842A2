//Jacky Tran 500766582
package eval;

import java.util.ArrayList;

public class Qrel {
	private String id = "";
	private ArrayList<String> docIDs = new ArrayList<String>();
	public Qrel(String id, ArrayList<String> docIDs) {
		this.id = id;
		this.docIDs = new ArrayList<String>(docIDs);
	}
	public Qrel() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<String> getDocIDs() {
		return docIDs;
	}
	public void setDocIDs(ArrayList<String> docIDs) {
		this.docIDs = docIDs;
	}
}

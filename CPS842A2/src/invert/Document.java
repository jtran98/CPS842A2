//Jacky Tran 500766582
package invert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//document object to help organize the cacm file
public class Document {
	private String id = "";
	private String title = "";
	private String articleAbstract = "";
	private String publicationDate = "";
	private String authorList = "";
	private String content = "";
	public Document(String id, String title, String articleAbstract, String publicationDate, String authorList) {
		this.id = id;
		this.title = title;
		this.articleAbstract = articleAbstract;
		this.publicationDate = publicationDate;
		this.authorList = authorList;
		this.content = title + "\n" + articleAbstract + "\n" + authorList;
	}
	public Document() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstract() {
		return articleAbstract;
	}
	public void setAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}
	public String getDate() {
		return publicationDate;
	}
	public void setDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	public String getAuthors() {
		return authorList;
	}
	public void setAuthors(String authorList) {
		this.authorList = authorList;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String[] toArray() {
		return this.content.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s+");
	}
	public ArrayList<Integer> getTermPositions(String term) {
		List<String> terms = Arrays.asList(this.content.split("\\s+")).stream().collect(Collectors.toList());
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for(int i = 0; i < terms.size(); i++) {
			if(terms.get(i).equalsIgnoreCase(term)) {
				positions.add(i);
			}
		}
		return positions;
	}
	public int getFrequencyOfTerm(String term) {
		int result = 0;
		List<String> terms = Arrays.asList(this.content.split("\\s+")).stream().collect(Collectors.toList());
		for(int i = 0; i < terms.size(); i++) {
			if(terms.get(i).equalsIgnoreCase(term)) {
				result++;
			}
		}
		return result;
	}
}
